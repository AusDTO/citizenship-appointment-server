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
test $DOCKER_COMPOSE_FILE_PATH    || failure DOCKER_COMPOSE_FILE_PATH
test -f $DOCKER_COMPOSE_FILE_PATH || failure DOCKER_COMPOSE_FILE_PATH
test $DOCKER_CONTAINER_NAME       || failure DOCKER_CONTAINER_NAME
test $DOCKER_IMAGE_NAME           || failure DOCKER_IMAGE_NAME
test $RABBITMQ_HOST               || failure RABBITMQ_HOST
test $RABBITMQ_PORT               || failure RABBITMQ_PORT
test $RABBITMQ_USER               || failure RABBITMQ_USER
test $RABBITMQ_PASSWORD           || failure RABBITMQ_PASSWORD
test $RABBITMQ_VHOST              || failure RABBITMQ_VHOST
echo "All environment variables are supplied"

set -e

export IMAGE_TAG=$(git show-ref -s refs/remotes/origin/HEAD)

echo "Creating a new ECS template and updating the service"

ecs-cli configure --cluster monitoring
ecs-cli compose --file $DOCKER_COMPOSE_FILE_PATH --project-name $DOCKER_CONTAINER_NAME service up

