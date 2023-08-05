plugins {
    id("java-library")
    id("checkstyle")
    id("maven-publish")
    id("signing")
    id("java-test-fixtures")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
    withJavadocJar()
}

checkstyle {
    configDirectory.set(File("../checkstyle"))
    toolVersion = "9.3"
}

group = "io.github.almighty-satan.jaskl"
version = "1.3.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation(testFixtures(project(":core")))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            pom {
                name.set("JASKL")
                description.set("Just Another Simple Config Library")
                url.set("https://github.com/Almighty-Satan/JASKL")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License v2.1")
                        url.set("https://opensource.org/license/lgpl-2-1/")
                    }
                }
                developers {
                    developer {
                        name.set("Almighty-Satan")
                        url.set("https://github.com/Almighty-Satan")
                    }
                    developer {
                        name.set("UeberallGebannt")
                        url.set("https://github.com/UeberallGebannt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Almighty-Satan/JASKL.git")
                    developerConnection.set("scm:git:ssh://github.com:Almighty-Satan/JASKL.git")
                    url.set("https://github.com/Almighty-Satan/JASKL")
                }
            }
            artifactId = "jaskl-${project.name}"
        }
        repositories {
            maven {
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("OSSRH_USER")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications.getByName("release"))
}
