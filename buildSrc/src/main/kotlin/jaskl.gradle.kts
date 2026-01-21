plugins {
    id("java-library")
    id("checkstyle")
    id("maven-publish")
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

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

dependencies {
    val annotations = libs.findLibrary("annotations").get()
    compileOnly(annotations)
    testFixturesCompileOnly(annotations)
    testCompileOnly(annotations)

    val junit = libs.findLibrary("junit").get()
    testImplementation(junit)
    testFixturesImplementation(junit)
    testRuntimeOnly(libs.findLibrary("junitplatformlauncher").get())

    testImplementation(testFixtures(project(":core")))
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
                        name.set("LeStegii")
                        url.set("https://github.com/LeStegii")
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
                setUrl(rootProject.layout.buildDirectory.dir("staging-deploy"))
            }
        }
    }
}
