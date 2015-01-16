/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.vlad.plugin

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.*
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import net.vlad.plugin.EclipseGradleNaturePlugin

/**
 *
 * @author vlad
 */
class EclipseGradleNaturePluginTest {

    @Test
    public void addPluginToProject() {
        Project project = ProjectBuilder
        .builder()
        .withProjectDir(new File("src/test/resources/testproject"))
        .build()
        //project.apply plugin: 'net.vlad.eclipse-gradle-nature'
        //project.apply plugin: 'net.vlad.plugin.EclipseGradleNaturePlugin'
        project.apply plugin: 'java'
        project.apply plugin: EclipseGradleNaturePlugin
        
        
        //assertTrue(project.tasks.hello instanceof GreetingTask)
        assertNotNull project.eclipse.classpath.file
    }
}
