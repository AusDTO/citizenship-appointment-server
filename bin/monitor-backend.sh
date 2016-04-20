#!/usr/bin/env bash

set -ef -o pipefail

curl -k -s -o /dev/null -u "${SECURITY_ADMIN_USERNAME}":"${SECURITY_ADMIN_PASSWORD}" -w "%{http_code}\\n" "${MONITOR_BACKEND_URL}" | grep 200
