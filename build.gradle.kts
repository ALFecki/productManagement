plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.kotlin.kapt") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.6.2"
    id("io.micronaut.test-resources") version "3.6.2"
}

version = "0.1"
group = "management"

val kotlinVersion= project.properties["kotlinVersion"]
repositories {
    mavenCentral()
}

dependencies {
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut:micronaut-validation")

    implementation("jakarta.annotation:jakarta.annotation-api")

    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")

    runtimeOnly("ch.qos.logback:logback-classic")

    runtimeOnly("org.postgresql:postgresql")

    implementation("pl.allegro.finance:tradukisto:1.12.0")
    implementation("com.vladmihalcea:hibernate-types-52:2.20.0")

    implementation("org.apache.poi:poi:5.2.2")
    implementation("org.apache.poi:poi-ooxml:5.2.2")

    implementation("fr.opensagres.xdocreport:fr.opensagres.poi.xwpf.converter.core:2.0.4")
    implementation("fr.opensagres.xdocreport:fr.opensagres.poi.xwpf.converter.pdf:2.0.4")

    implementation("com.itextpdf:itext7-core:7.2.3")
    implementation("com.itextpdf:html2pdf:4.0.3")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

}


application {
    mainClass.set("management.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("management.*")
    }
}



