run:
	docker-compose up --build

run-podman:
	podman compose up --build

unit-test:
	./gradlew test

integration-test:
	./gradlew integrationTest
