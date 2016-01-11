#!/bin/bash

# Exit immediately if any commands return non-zero
set -e
# Output the commands we run
set -x

# This is a modified version of the Cloud Foundry Blue/Green deployment guide:
#
# https://docs.pivotal.io/pivotalcf/devguide/deploy-apps/blue-green.html
#
# To ensure a site can be served from gov-au.cfapps.io when updates are being
# pushed, we run two instances of the application (blue and green). To update
# the site, we change the blue application, then change the green.

# Update the blue app
cf unmap-route citizenship-appointment-beta-blue cfapps.io -n citizenship-appointment-beta.cfapps.io
cf push citizenship-appointment-beta-blue --no-hostname --no-manifest --no-route -p build/libs/citizenship-appointments-0.0.1.jar -i 1 -m 512M
cf map-route citizenship-appointment-beta-blue cfapps.io -n citizenship-appointment-beta.cfapps.io

# Update the green app
cf unmap-route citizenship-appointment-beta-green cfapps.io -n citizenship-appointment-beta.cfapps.io
cf push citizenship-appointment-beta-green --no-hostname --no-manifest --no-route -p build/libs/citizenship-appointments-0.0.1.jar -i 1 -m 512M
cf map-route citizenship-appointment-beta-green cfapps.io -n citizenship-appointment-beta.cfapps.io
