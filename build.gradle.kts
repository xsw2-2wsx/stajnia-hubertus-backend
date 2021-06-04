import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.0"
	kotlin("plugin.spring") version "1.5.0"
	kotlin("plugin.jpa") version "1.5.0"
	id("org.hidetake.swagger.generator") version "2.18.2"
}

group = "net.jupw"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	swaggerCodegen("io.swagger.codegen.v3:swagger-codegen-cli:3.0.26")
	implementation("io.swagger.core.v3:swagger-core:2.1.9")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")

	implementation("io.jsonwebtoken:jjwt-api:0.11.1")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.1")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
	testImplementation("org.mockito:mockito-core:3.+")
	testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

	configurations {
		all {
			exclude(module = "spring-boot-starter-logging")
		}
	}
}

swaggerSources {
	register("hubertus") {
		setInputFile(file("api/openapi.yaml"))
		code.apply {
			language = "spring"
			configFile = file("swagger-codegen-spring-config.json")
			outputDir = file("build/generated/")
		}
	}
}

sourceSets {
	main.configure {
		java {
			srcDir("build/generated/src/main/java")
		}
	}
}

tasks.bootRun.configure {
	systemProperty("spring.profiles.active", "dev")
}

tasks.test.configure {
	systemProperty("spring.profiles.active", "dev")
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
