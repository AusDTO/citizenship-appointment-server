PROJECT_NAME := $(shell basename `pwd`)
DATE_STAMP   := $(shell date +%Y%m%d-%H%M%S)

.PHONY: build
build:
	docker build -t $(PROJECT_NAME):$(DATE_STAMP) .
	docker tag $(PROJECT_NAME):$(DATE_STAMP) $(PROJECT_NAME):latest
	docker tag $(PROJECT_NAME):$(DATE_STAMP) 048212923711.dkr.ecr.us-east-1.amazonaws.com/ausdtomonitoring-$(PROJECT_NAME):$(DATE_STAMP)
	docker tag $(PROJECT_NAME):$(DATE_STAMP) 048212923711.dkr.ecr.us-east-1.amazonaws.com/ausdtomonitoring-$(PROJECT_NAME):latest

.PHONY: push
push:
	docker push 048212923711.dkr.ecr.us-east-1.amazonaws.com/ausdtomonitoring-$(PROJECT_NAME):latest

.PHONY: run-bash
run-bash:
	docker run --rm -it --link=monitoring-stack-sensu-server:sensu-server  $(PROJECT_NAME):latest /bin/bash

.PHONY: run
run:
	docker run --rm -it --link=monitoring-stack-sensu-server:sensu-server --name=$(PROJECT_NAME) $(PROJECT_NAME):latest

.PHONY: deploy
deploy:
	docker run --rm  -d --link=monitoring-stack-sensu-server:sensu-server --name=$(PROJECT_NAME) $(PROJECT_NAME):latest
