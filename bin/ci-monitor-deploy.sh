#!/bin/bash

failure(){
  echo "ERROR - \$$1 not set"
  exit 1
}

echo "Validate required environment variables"
test $AWS_ACCOUNT_ID              || failure AWS_ACCOUNT_ID
test $AWS_REGION                  || failure AWS_REGION
test $AWS_ACCESS_KEY_ID           || failure AWS_ACCESS_KEY_ID
test $AWS_SECRET_ACCESS_KEY       || failure AWS_SECRET_ACCESS_KEY
# test $DOCKERFILE_PATH             || failure DOCKERFILE_PATH
test $DOCKER_COMPOSE_FILE_PATH    || failure DOCKER_COMPOSE_FILE_PATH
test -f $DOCKER_COMPOSE_FILE_PATH || failure DOCKER_COMPOSE_FILE_PATH
test $RABBITMQ_HOST               || failure RABBITMQ_HOST
test $RABBITMQ_PORT               || failure RABBITMQ_PORT
test $RABBITMQ_USER               || failure RABBITMQ_USER
test $RABBITMQ_PASSWORD           || failure RABBITMQ_PASSWORD
test $RABBITMQ_VHOST              || failure RABBITMQ_VHOST
echo "All environment variables are supplied"

set -e

GIT_REF=$(git show-ref -s refs/remotes/origin/HEAD)

echo "Creating a new ECS template and updating the service"
sed -ie "s/IMAGE_TAG/$GIT_REF/" $DOCKER_COMPOSE_FILE_PATH
sed -ie "s/AWS_ACCOUNT_ID/$AWS_ACCOUNT_ID/" $DOCKER_COMPOSE_FILE_PATH
sed -ie "s/AWS_REGION/$AWS_REGION/" $DOCKER_COMPOSE_FILE_PATH
sed -ie "s/AWS_ACCOUNT_ID/$AWS_ACCOUNT_ID/" $DOCKER_COMPOSE_FILE_PATH

ecs-cli configure --cluster monitoring
ecs-cli compose -f $DOCKER_COMPOSE_FILE_PATH service up

