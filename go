#!/usr/bin/env bash

set -ef -o pipefail

case "$1" in
        help)
            echo $"Usage: $0 {fullBuild | test | integrationTest | allTests | startApp }"
            echo $"Any other command: $0 *something* will execute ./gradlew *something* "
            exit 1
            ;;
        clean)
            ./gradlew clean
            ;;
        fullBuild)
            ./gradlew clean build
            ;;
        fullBuildVerbose)
            ./gradlew clean build --info
            ;;
        test)
            ./gradlew test
            ;;
        integrationTest)
            ./gradlew integrationTest
            ;;
        allTests)
            ./gradlew check
            ;;
        startApp)
            ./gradlew run
            ;;
        *)
            ./gradlew $@
            ;;
esac
