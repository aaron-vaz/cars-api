plugins {
    java

    id("org.springframework.boot").version("2.5.4")
    id("io.spring.dependency-management").version("1.0.11.RELEASE")
    id("com.google.cloud.tools.jib").version("3.1.4")
}

version = "0.0.1-SNAPSHOT"

repositories {
    maven("https://maven-central-eu.storage-download.googleapis.com/maven2/")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

jib {
    from.image = "gcr.io/distroless/java:11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    test {
        useJUnitPlatform()
    }
}
