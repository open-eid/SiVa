#!/usr/bin/env bash

# NOTE: httpie and pup must be installed
# httpie - https://github.com/jkbrzt/httpie
# pup - https://github.com/EricChiang/pup

source ./helper-scripts.conf

LOG_FILE='dss-change.log' 
TASKS_FILE='tasks.txt'
TASK_TITLES_FILE='task-titles.txt'
OUTPUT_FILE='tasks-combined.log'

# Get all JIRA task numbers from git log 
grep -o 'VAL\-[0-9]\{1,2\}' ${LOG_FILE} | sort | uniq > ${TASKS_FILE}

# Add URL to JIRA task
sed -i -e $JIRA_URL_PREFIX ${TASKS_FILE} 

echo 'Start getting task titles...'
rm -f $TASK_TITLES_FILE
while read url; do
    echo 'Getting url:' $url
    http --ignore-stdin GET https://$USERNAME:$PASSWORD@`echo $url | grep -o 'jira.*$'` | pup 'title text{}' >> $TASK_TITLES_FILE
done < $TASKS_FILE

echo 'Finished reading task titles'
sed -i -e "s/\.*\[.*\] \(.*\)\- SK JIRA.*/\1/" $TASK_TITLES_FILE

paste $TASKS_FILE $TASK_TITLES_FILE > $OUTPUT_FILE 

# clean up
rm $TASKS_FILE $TASK_TITLES_FILE $TASKS_FILE-e $TASK_TITLES_FILE-e
