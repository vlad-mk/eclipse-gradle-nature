eclipse-gradle-nature
======================

[![Build Status](https://travis-ci.org/vlad-mk/eclipse-gradle-nature.svg)](https://travis-ci.org/vlad-mk/eclipse-gradle-nature)
[![Download](https://api.bintray.com/packages/vlad-mk/gradle-plugins/eclipse-gradle-nature/images/download.svg) ](https://bintray.com/vlad-mk/gradle-plugins/eclipse-gradle-nature/_latestVersion)

#Overview

[Gradle](http://www.gradle.org) plugin extending the standard [eclipse](http://gradle.org/docs/current/userguide/eclipse_plugin.html) plugin to add gradle nature to an eclipse project.


downloading
===========

this plugin can be downloaded from [plugins.gradle.org](http://plugins.gradle.org/plugin/net.vlad.eclipse-gradle-nature)

usage
=====

Apply plugin **gradle 2.1+** style:

```groovy
buildscript {
    repositories {
        mavenCentral() // for plugin dependencies
    }
}

plugins {
    id 'net.vlad.eclipse-gradle-nature' version '0.1.2'
}
```

If using gradle < 2.1:

```groovy
buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath group: 'net.vlad', name: 'eclipse-gradle-nature', version: '0.1.2'
    }
}

apply plugin: 'net.vlad.eclipse-gradle-nature'
```


## Tasks

Plugin adds following tasks to the `IDE tasks` group

* `eclipseGradle` - Generates Eclipse Gradle configuration files.
* `cleanEclipseGradle` - Cleans Eclipse Gradle configuration files.

which will be called by `eclise` and `cleanEclipse` tasks

