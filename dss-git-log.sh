#!/usr/bin/env bash

AUTHORS_LIST='\(Mihkel\)\|\(Risto\)\|\(andresw\)\|\(andres.voll\)'
GIT_FORMAT='Commiter\:\ %cn%nMessage: %s'
# 
git log --since=2015-03-01 --pretty=format:'Commiter: %cn%nMessage: %s%nDate: %ad%n'  --author='\(Mihkel\)\|\(Risto\)\|\(andresw\)\|\(andres.voll\)'  --name-status -- . ":(exclude)pdf-validator-*" ":(exclude,icase)*Test*" > dss-change.log
