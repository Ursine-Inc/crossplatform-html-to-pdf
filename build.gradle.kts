plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.14"
    id("org.beryx.jlink") version "3.0.1"
}

group = "com.ursineenterprises.utilities"
version = "1.0.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.ursineenterprises.utilities.htmltopdf")
    mainClass.set("com.ursineenterprises.utilities.htmltopdf.HelloApplication")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-fxml:21")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.openhtmltopdf:openhtmltopdf-core:1.0.10")
    implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")
    implementation("com.openhtmltopdf:openhtmltopdf-jsoup-dom-converter:1.0.0")
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude(group = "org.openjfx")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress=zip-6", "--no-header-files", "--no-man-pages"))
    launcher {
        name = project.findProperty("appName")?.toString() ?: "HTMLtoPDF"
    }

    jpackage {
        val targetOs = project.findProperty("targetOs")?.toString()?.lowercase()
        val currentOs = System.getProperty("os.name").lowercase()

        // Determine if we're doing a native build (same OS) or cross-platform
        val isNativeBuild = when {
            targetOs == null -> true // No targetOs specified, build for current OS
            targetOs == "windows" && currentOs.contains("win") -> true
            targetOs == "mac" && currentOs.contains("mac") -> true
            targetOs == "linux" && currentOs.contains("linux") -> true
            else -> false
        }

        imageName = project.findProperty("appName")?.toString() ?: "HTMLtoPDF"

        // Only create installers for native builds, app-image for cross-platform
        if (isNativeBuild) {
            installerType = when {
                currentOs.contains("win") -> "exe"
                currentOs.contains("mac") -> "dmg"
                currentOs.contains("linux") -> "deb"
                else -> "app-image"
            }

            installerOptions = when {
                currentOs.contains("win") -> listOf(
                    "--win-menu",
                    "--win-shortcut"
                )
                currentOs.contains("mac") -> emptyList()
                else -> emptyList()
            }
        } else {
            // For cross-platform builds, just create app-image (no installer)
            skipInstaller = true
        }
    }
}
