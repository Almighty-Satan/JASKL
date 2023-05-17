plugins {
    jaskl
}

dependencies {
    api(project(":core"))
    implementation("org.mongodb:mongodb-driver-sync:4.9.0")
}
