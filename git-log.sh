#!/usr/bin/env bash

AUTHORS_LIST='\(Mihkel\)\|\(Risto\)\|\(andresw\)\|\(andres.voll\)'
GIT_FORMAT='Commiter\:\ %cn%nMessage: %s'
PERIOD=$1
 
git log --since=$PERIOD --pretty=format:'Commiter: %cn%nMessage: %s%nDate: %ad%n'  --author='\(Mihkel\)\|\(Risto\)\|\(andresw\)\|\(andres.voll\)'  --name-status -- . ":(exclude,icase)*Test*" > git-change.log
