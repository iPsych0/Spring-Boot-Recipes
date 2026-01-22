plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "11.20.2"
}

group = "com.abn"
version = "0.0.1-SNAPSHOT"
description = "Spring Boot project for discovering & managing recipes"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

sourceSets {
	val integrationTest by creating {
		java.srcDir("src/integration-test/java")
		resources.srcDir("src/integration-test/resources")

		compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
		runtimeClasspath += output + compileClasspath
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")
    implementation("org.hibernate.validator:hibernate-validator:9.1.0.Final")

    implementation("org.flywaydb:flyway-core:11.20.2")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.20.2")
    runtimeOnly("org.postgresql:postgresql:42.7.8")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.testcontainers:junit-jupiter:1.21.4")
	testImplementation("org.testcontainers:postgresql:1.21.4")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val integrationTest = tasks.register<Test>("integrationTest") {
	description = "Runs integration tests."
	group = "verification"

	val integrationSourceSet = sourceSets["integrationTest"]

	testClassesDirs = integrationSourceSet.output.classesDirs
	classpath = integrationSourceSet.runtimeClasspath

	shouldRunAfter("test")
}

tasks.named("check") {
	dependsOn(integrationTest)
}