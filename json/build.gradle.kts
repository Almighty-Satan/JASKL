plugins {
    jaskl
}

dependencies {
    api(project(":jackson"))
    implementation(libs.jackson.databind)
}
