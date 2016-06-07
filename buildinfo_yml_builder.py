#!/usr/bin/env python3

import yaml
import sys

git_commit = sys.argv[1]
travis_job_id = sys.argv[2]
travis_build_number = sys.argv[3]

git_base_url = 'https://github.com/open-eid/SiVa/commit/'
travis_base_url = 'https://travis-ci.org/open-eid/SiVa/builds/'

build_info = dict(
    github=dict(
        url=git_base_url + git_commit,
        shortHash=git_commit[:8]
    ),
    travisCi=dict(
        buildUrl=travis_base_url + travis_job_id,
        buildNumber='#' + travis_build_number
    )
)

with open('ci-build-info.yml', 'w') as outfile:
    outfile.write(yaml.dump(build_info, default_flow_style=False))
