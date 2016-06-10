#!/usr/bin/env python3

import yaml
import sys
import requests

git_commit = sys.argv[1]
travis_job_id = sys.argv[2]
travis_build_number = sys.argv[3]

git_commit_api_url = "https://api.github.com/repos/open-eid/SiVa/git/commits/"
git_base_url = 'https://github.com/open-eid/SiVa/commit/'
travis_base_url = 'https://travis-ci.org/open-eid/SiVa/builds/'


def git_commit_response():
    req = requests.get(git_commit_api_url + git_commit)
    return req.json()


def commit_author():
    default_author = 'Unknown'
    api_response = git_commit_response()

    if 'committer' in api_response:
        return api_response['committer']['name']
    else:
        return default_author


build_info = dict(
    github=dict(
        url=git_base_url + git_commit,
        shortHash=git_commit[:8],
        authorName=commit_author()
    ),
    travisCi=dict(
        buildUrl=travis_base_url + travis_job_id,
        buildNumber='#' + travis_build_number
    )
)

with open('ci-build-info.yml', 'w') as outfile:
    outfile.write( yaml.dump(build_info, default_flow_style=False))