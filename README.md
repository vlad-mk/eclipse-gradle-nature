eclipse-gradle-nature
======================

[![Build Status](https://travis-ci.org/vlad-mk/eclipse-gradle-nature.svg)](https://travis-ci.org/vlad-mk/eclipse-gradle-nature)
[![Download](https://api.bintray.com/packages/vlad-mk/gradle-plugins/eclipse-gradle-nature/images/download.svg) ](https://bintray.com/vlad-mk/gradle-plugins/eclipse-gradle-nature/_latestVersion)

#Overview

[Gradle](http://www.gradle.org) plugin extending the standard [eclipse](http://gradle.org/docs/current/userguide/eclipse_plugin.html) plugin to add gradle nature to eclipse projects.
[Gradle Tooling](http://marketplace.eclipse.org/content/gradle-integration-eclipse) musst be installed in eclipse to use such projects.

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
    id 'net.vlad.eclipse-gradle-nature' version '0.2.0'
}
```

Using old gradle style:

```groovy
buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath group: 'net.vlad', name: 'eclipse-gradle-nature', version: '0.2.0'
    }
}

apply plugin: 'net.vlad.eclipse-gradle-nature'
```


## Tasks

Plugin adds following tasks to the `IDE tasks` group

* `eclipseGradle` - Generates Eclipse Gradle configuration files.
* `cleanEclipseGradle` - Cleans Eclipse Gradle configuration files.

which will be called by `eclise` and `cleanEclipse` tasks

