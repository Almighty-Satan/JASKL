plugins {
    jaskl
}

dependencies {
    api(project(":jackson"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.18.2")
}
