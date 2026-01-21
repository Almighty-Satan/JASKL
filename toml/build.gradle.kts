plugins {
    jaskl
}

dependencies {
    api(project(":jackson"))
    implementation(libs.jackson.dataformat.toml)
}
