plugins {
    jaskl
}

dependencies {
    api(project(":core"))
    implementation(libs.jackson.databind)
}