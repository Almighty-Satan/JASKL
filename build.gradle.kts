import org.jreleaser.model.Active
import org.jreleaser.model.api.deploy.maven.MavenCentralMavenDeployer

plugins {
    id("java-library")
    id("maven-publish")
    id("org.jreleaser")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

jreleaser {
    signing {
        active = Active.RELEASE
        armored = true
    }
    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    stage = MavenCentralMavenDeployer.Stage.UPLOAD
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
    release {
        github {
            skipRelease = true
            token = "none"
        }
    }
}