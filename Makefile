.PHONY: run run-podman unit-test integration-test test setup-keycloak get-token clean build

run:
	docker-compose up --build

run-podman:
	podman compose up --build

run-detached:
	docker-compose up --build -d

stop:
	docker-compose down

unit-test:
	./gradlew test

integration-test:
	./gradlew integrationTest

test:
	./gradlew check

clean:
	./gradlew clean

build:
	./gradlew build -x integrationTest

setup-keycloak:
	./scripts/get-token.sh setup

get-token:
	./scripts/get-token.sh
