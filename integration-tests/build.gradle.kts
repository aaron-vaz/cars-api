plugins {
    java

    id("org.springframework.boot").version("2.5.4")
    id("io.spring.dependency-management").version("1.0.11.RELEASE")
}

repositories {
    maven("https://maven-central-eu.storage-download.googleapis.com/maven2/")
}

dependencies {
    testImplementation(project(":server"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:3.0.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    bootJar {
        enabled = false
    }

    test {
        useJUnitPlatform()
    }
}
