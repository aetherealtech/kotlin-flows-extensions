plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
    signing
}

group = "com.aetherealtech"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.aetherealtech:tuples:0.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(11)
}

val NEXUS_USERNAME: String by properties
val NEXUS_PASSWORD: String by properties

publishing {
    repositories {
        maven {
            credentials {
                username = NEXUS_USERNAME
                password = NEXUS_PASSWORD
            }
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
    }

    publications {
        create<MavenPublication>("Maven") {
            from(components["java"])
        }
        withType<MavenPublication> {
            pom {
                packaging = "jar"
                name.set("kotlinflowsextensions")
                description.set("Kotlin Flows Extensions")
                url.set("github.com/aetherealtech/kotlin-flows-extensions")
                licenses {
                    license {
                        name.set("GPLv3 license")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/aetherealtech/kotlin-flows-extensions/issues")
                }
                scm {
                    connection.set("scm:git:git://github.com/aetherealtech/kotlin-flows-extensions.git")
                    developerConnection.set("scm:git:git@github.com:aetherealtech/kotlin-flows-extensions.git")
                    url.set("https://github.com/aetherealtech/kotlin-flows-extensions")
                }
                developers {
                    developer {
                        name.set("Dan Coleman")
                        email.set("dan@aetherealtech.com")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}