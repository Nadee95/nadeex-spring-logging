plugins {
    `java-library`
    `maven-publish`
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.spring.dependency-management") version "1.1.4"
    jacoco
}

group = "com.nadeex.spring"
version = "0.1.0"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages-Common"
        url = uri("https://maven.pkg.github.com/Nadee95/nadeex-spring-common")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    // Uses CommonConstants for header names
    api("com.nadeex.spring:common:0.1.0")

    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Structured JSON logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

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
            pom {
                name.set("Nadeex Spring Logging")
                description.set("Structured logging with MDC, correlation IDs, and AOP for Spring Boot")
                url.set("https://github.com/Nadee95/nadeex-spring-logging")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("nadee95")
                        name.set("Nadee")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Nadee95/nadeex-spring-logging.git")
                    developerConnection.set("scm:git:ssh://github.com/Nadee95/nadeex-spring-logging.git")
                    url.set("https://github.com/Nadee95/nadeex-spring-logging")
                }
            }
        }
    }
    repositories {
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
