#!/bin/bash

# Exit immediately if any commands return non-zero
set -e
# Output the commands we run
set -x

# This is a modified version of the Cloud Foundry Blue/Green deployment guide:
#
# https://docs.pivotal.io/pivotalcf/devguide/deploy-apps/blue-green.html

# Update the blue app
cf unmap-route citizenship-appointment-beta-blue appointments.border.gov.au
cf push citizenship-appointment-beta-blue -b https://github.com/AusDTO/java-buildpack.git --no-hostname --no-manifest --no-route -p build/libs/citizenship-appointments-0.0.1.jar -i 1 -m 512M
cf map-route citizenship-appointment-beta-blue appointments.border.gov.au

# Update the green app
cf unmap-route citizenship-appointment-beta-green appointments.border.gov.au
cf push citizenship-appointment-beta-green -b https://github.com/AusDTO/java-buildpack.git --no-hostname --no-manifest --no-route -p build/libs/citizenship-appointments-0.0.1.jar -i 1 -m 512M
cf map-route citizenship-appointment-beta-green appointments.border.gov.au

