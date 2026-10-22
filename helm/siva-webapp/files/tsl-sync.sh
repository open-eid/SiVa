#!/bin/sh
# The trust-list snapshot store bridged through object storage with rclone (tsl.snapshot.sync).
# SiVa only ever reads and writes a directory, so the bucket is a mirror of the store in the same
# layout (current, snapshots/<id>/, work/) and this script moves it, in the fetcher pod and in
# every webapp pod, with the ordering a store copied object by object needs: a snapshot's files
# before the `current` that names it, both ways.
#
#   tsl-sync.sh down   fetcher pod, init container: mirror the bucket into the pod's store, so
#                      the fetch sees the last snapshot (prune, lock) and its working cache
#                      (a list whose download fails keeps its last copy)
#   tsl-sync.sh up     fetcher pod, beside the fetch: wait until the fetcher's JVM is gone (the
#                      pod shares its process namespace), then publish the store: snapshots
#                      first, current last, the working cache after
#   tsl-sync.sh pod    webapp pod, sidecar: every INTERVAL seconds fetch the snapshot the
#                      bucket's current names and, once it is complete, point the pod's own
#                      current at it; SiVa polls that file as it does on a shared volume
#
# Environment: STORE (the store directory), REMOTE (rclone path of the bucket prefix, e.g.
# bucket:siva/tsl), INTERVAL (seconds between downloads, pod), KEEP (snapshots a pod keeps
# besides the current one), FETCH_START_TIMEOUT (seconds the uploader waits for the fetch to
# start), RCLONE_CONFIG and whatever RCLONE_* flags the remote needs. Needs rclone's own image
# (busybox: sh, pgrep, sed) or one with the same tools.
set -eu

mode=${1:?mode: down, up or pod}
: "${STORE:?}" "${REMOTE:?}"
: "${INTERVAL:=60}" "${KEEP:=1}" "${FETCH_START_TIMEOUT:=120}"
# The fetcher's lock is this pod's business only; a staging directory is a publish that crashed.
excludes="--exclude fetch.lock --exclude *.tmp --exclude *.tmp/**"

log() { echo "tsl-sync: $*"; }

# rclone's exit 3 is "directory not found": a bucket or prefix nothing has been published to yet.
down() {
  mkdir -p "$STORE"
  if rclone sync "$REMOTE" "$STORE" $excludes; then
    log "store mirrored from $REMOTE, current $(cat "$STORE/current" 2>/dev/null || echo none)"
  else
    status=$?
    [ "$status" -eq 3 ] || exit "$status"
    log "$REMOTE holds no store yet"
  fi
}

up() {
  waited=0
  until pgrep -x java >/dev/null; do
    if [ "$waited" -ge "$FETCH_START_TIMEOUT" ]; then
      log "no fetch started within $FETCH_START_TIMEOUT s; publishing the store as it is"
      break
    fi
    sleep 1; waited=$((waited + 1))
  done
  while pgrep -x java >/dev/null; do sleep 2; done
  if [ -d "$STORE/snapshots" ]; then rclone sync "$STORE/snapshots" "$REMOTE/snapshots" $excludes; fi
  if [ -f "$STORE/current" ]; then rclone copyto "$STORE/current" "$REMOTE/current"; fi
  if [ -d "$STORE/work" ]; then rclone sync "$STORE/work" "$REMOTE/work"; fi
  log "store published to $REMOTE, current $(cat "$STORE/current" 2>/dev/null || echo none)"
}

# One download: the id the bucket's current names, then that snapshot, then the pod's current.
# A snapshot is current only when every file its manifest counts is there; until then (the
# publisher is still uploading, or a download broke off) the loaded one stays and the next round
# tries again. SiVa checks the manifest's file list itself as well.
pod_once() {
  id=$(rclone cat "$REMOTE/current" 2>/dev/null | tr -d '[:space:]') || id=""
  if [ -z "$id" ]; then log "nothing published in $REMOTE yet"; return 0; fi
  case "$id" in *[!A-Za-z0-9_-]*) log "ignoring current '$id': not a snapshot id"; return 1 ;; esac
  if [ "$(cat "$STORE/current" 2>/dev/null)" = "$id" ]; then return 0; fi
  dir=$STORE/snapshots/$id
  rclone sync "$REMOTE/snapshots/$id" "$dir" || { log "snapshot $id: download failed"; return 1; }
  want=$(sed -n 's/^.*"files" *: *\([0-9][0-9]*\).*$/\1/p' "$dir/manifest.json" 2>/dev/null | head -n 1)
  have=$(ls -1 "$dir" | grep -vc '^manifest.json$' || true)
  if [ -z "$want" ] || [ "$have" != "$want" ]; then
    log "snapshot $id is incomplete ($have of ${want:-?} files), still being published"
    return 1
  fi
  printf '%s\n' "$id" > "$STORE/current.tmp" && mv -f "$STORE/current.tmp" "$STORE/current"
  log "snapshot $id ($have files) is current"
  ls -1 "$STORE/snapshots" | grep -vx "$id" | sort -r | tail -n +$((KEEP + 1)) | while read -r old; do
    rm -rf "$STORE/snapshots/$old" && log "pruned $old"
  done
}

pod() {
  mkdir -p "$STORE/snapshots"
  while :; do
    pod_once || log "retrying in $INTERVAL s"
    sleep "$INTERVAL"
  done
}

case "$mode" in
  down) down ;;
  up) up ;;
  pod) pod ;;
  *) echo "tsl-sync: unknown mode '$mode' (down, up or pod)" >&2; exit 2 ;;
esac
