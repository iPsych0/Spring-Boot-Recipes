run:
	docker-compose up --build

unit-test:
	./gradlew test

integration-test:
	./gradlew integrationTest
