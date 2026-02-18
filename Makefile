run:
	docker-compose up --build

run-podman:
	podman compose up --build

unit-test:
	./gradlew test

integration-test:
	./gradlew integrationTest

setup-keycloak:
	./scripts/get-token.sh setup

get-token:
	./scripts/get-token.sh

