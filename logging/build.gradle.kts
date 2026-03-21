plugins {
    `java-library`
    `maven-publish`
    id("io.spring.dependency-management") version "1.1.4"
    jacoco
}

group = "com.nadeex.spring"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.3")
    }
}

dependencies {
    api("com.nadeex.spring:common:0.1.0")

    // Spring (compileOnly - provided by consuming apps)
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")

    // Structured JSON logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-aop")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.test { finalizedBy(tasks.jacocoTestReport) }

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.nadeex.spring"
            artifactId = "logging"
            version = "0.1.0"
            from(components["java"])

            versionMapping {
                usage("java-api") { fromResolutionOf("runtimeClasspath") }
                usage("java-runtime") { fromResolutionResult() }
            }
        }
    }
    repositories {
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Nadee95/nadeex-spring-logging")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

