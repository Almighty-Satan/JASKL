plugins {
    jaskl
}

dependencies {
    api(project(":core"))
    implementation("org.mongodb:mongodb-driver-sync:5.5.0")
}
