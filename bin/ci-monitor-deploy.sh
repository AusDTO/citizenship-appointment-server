#!/bin/bash

failure(){
  echo "ERROR - \$$1 not set"
  exit 1
}

echo "Validate required environment variables"
test $AWS_ACCOUNT_ID                  || failure AWS_ACCOUNT_ID
test $AWS_REGION                      || failure AWS_REGION
test $AWS_ACCESS_KEY_ID               || failure AWS_ACCESS_KEY_ID
test $AWS_SECRET_ACCESS_KEY           || failure AWS_SECRET_ACCESS_KEY
test $DOCKER_COMPOSE_TEMPLATE_PATH    || failure DOCKER_COMPOSE_TEMPLATE_PATH
test -f $DOCKER_COMPOSE_TEMPLATE_PATH || failure DOCKER_COMPOSE_TEMPLATE_PATH
test $DOCKER_CONTAINER_NAME           || failure DOCKER_CONTAINER_NAME
test $DOCKER_IMAGE_NAME               || failure DOCKER_IMAGE_NAME
test $RABBITMQ_HOST                   || failure RABBITMQ_HOST
test $RABBITMQ_PORT                   || failure RABBITMQ_PORT
test $RABBITMQ_USER                   || failure RABBITMQ_USER
test $RABBITMQ_PASSWORD               || failure RABBITMQ_PASSWORD
test $RABBITMQ_VHOST                  || failure RABBITMQ_VHOST
echo "All environment variables are supplied"

set -e

GIT_REF=$(git show-ref -s refs/remotes/origin/HEAD)
DOCKER_COMPOSE_FILE_PATH=${DOCKER_COMPOSE_TEMPLATE_PATH%.template*}

echo "Creating a new ECS template and updating the service"
sed -e "s/IMAGE_TAG/$GIT_REF/" \
    -e "s/AWS_ACCOUNT_ID/$AWS_ACCOUNT_ID/" \
    -e "s/AWS_REGION/$AWS_REGION/" \
    -e "s/AWS_ACCOUNT_ID/$AWS_ACCOUNT_ID/" \
    -e "s/DOCKER_CONTAINER_NAME/$DOCKER_CONTAINER_NAME/" \
    -e "s/DOCKER_IMAGE_NAME/$DOCKER_IMAGE_NAME/" \
    $DOCKER_COMPOSE_TEMPLATE_PATH > $DOCKER_COMPOSE_FILE_PATH \
    /

ecs-cli configure --cluster monitoring
ecs-cli compose --file $DOCKER_COMPOSE_FILE_PATH --project-name $DOCKER_CONTAINER_NAME service up

rm -f $DOCKER_COMPOSE_FILE_PATH
