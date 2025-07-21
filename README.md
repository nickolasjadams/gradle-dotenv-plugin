# Gradle dotenv plugin

This plugin loads the dotenv file so that it can be referenced from build.gradle.

It dynamically loads based on environment, and allows to merge extra env files.

This is a fork of otkmnb2783's work in https://github.com/otkmnb2783/gradle-dotenv-plugin, but is expanded beyond just loadinmg ".env".

## Minimal supported versions

This plugin was written using the new API available for gradle script kotlin builds.
This API is available in new versions of gradle.

Minimal supported [Gradle](www.gradle.org) version: `4.10`

## Dot Env Files

Create .env files for your gradle project. 

Examples of names for these files are: `.env`, `.env.development`, `.env.production`, etc.

They are formatted like this.

```dotenv
# This is a comment.
; This is also a comment.
MYSQL_USER=
MYSQL_PASSWORD=
MYSQL_DATABASE=
```

## Simple setup

Build script snippet for use in all Gradle versions:

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.github.nickolasjadams.dotenv:gradle-dotenv-plugin:<current_version>"
  }
}

ext {
	envSystemProperty = 'property.you.want.to.check'
}

apply plugin: "io.github.nickolasjadams.dotenv"

flyway {	
    url = "jdbc:mysql://localhost:3306/${env.MYSQL_DATABASE}"	
    user = "${env.MYSQL_USER}"	
    password = "${env.MYSQL_PASSWORD}"	
}	

```


## Using new plugin API

Build script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:
```groovy
plugins {
  id "io.github.nickolasjadams.dotenv" version "<current_version>"
}
```

## Specify the System Property to check what environment to load

The default environment is `.env`. If you want to load a different environment, you can specify the system property you'd like to check and load.

Define what system property you'd like to check in the build.gradle file below the buildscript, but above the plugin declaration.

```groovy
ext {
    envSystemProperty = 'grails.environment'
}
```

The plugin will look for the environement in that property (e.g. "production").
Then it will look for the file `.env.production` in the project root directory.

You may also pass the system property in the command line when running gradle tasks like this:

```bash
./gradlew bootRun -Dgrails.environment=production
```

## Load / Merge multiple .env files

If you want to load an additional .env file, you can pass that as a system property when running gradle tasks as well:

```bash
./gradlew bootRun -Dgrails.environment=production -DenvExtraFile=.env.uat
```

This will load the extra file after the first file, and merge over the existing environment variables.