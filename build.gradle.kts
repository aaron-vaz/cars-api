plugins {
    id("com.diffplug.spotless").version("5.12.4")
    id("com.github.ben-manes.versions").version("0.38.0")
}

allprojects {
    group = "uk.co.aaronvaz"
}

spotless {
    java {
        removeUnusedImports()
        googleJavaFormat().aosp()
        target("**/*.java")
    }

    kotlinGradle {
        target("**/*.gradle.kts")
    }
}
