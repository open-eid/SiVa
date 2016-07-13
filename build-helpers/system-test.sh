#!/usr/bin/env bash

mkdir ${TRAVIS_BUILD_DIR}/artifacts && cp ${TRAVIS_BUILD_DIR}/siva-parent/siva-distribution/target/*.zip artifacts
mv ${TRAVIS_BUILD_DIR}/artifacts/*.zip ${TRAVIS_BUILD_DIR}/artifacts/distribution.zip && ls -al ${TRAVIS_BUILD_DIR}/artifacts
chmod 600 ${TRAVIS_BUILD_DIR}/ci-deploy-key

scp -i ci-deploy-key artifacts/distribution.zip ci-deploy@demoapps.eu:/home/ci-deploy
ssh -i ci-deploy-key ci-deploy@demoapps.eu "bash -s" < ./build-helpers/stage-deploy.sh
