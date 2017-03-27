/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.vlad.plugin

import groovy.util.logging.Log
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
@Log
class EclipseGradleNaturePluginTest {

    @Test
    public void addPluginToProject() {
        Project project = ProjectBuilder
        .builder()
//        .withProjectDir(new File("src/test/resources/testproject"))
        .build()
        //project.apply plugin: 'net.vlad.eclipse-gradle-nature'
        //project.apply plugin: 'net.vlad.plugin.EclipseGradleNaturePlugin'
        project.apply plugin: 'java'
        project.apply plugin: EclipseGradleNaturePlugin
        
        
        //assertTrue(project.tasks.hello instanceof GreetingTask)
        assertNotNull project.eclipse.classpath.file
        project.tasks['eclipse'].execute()
        project.tasks['clean'].execute()
    }

    @Test
    public void multyProject() {
        Project project = ProjectBuilder
                .builder().withName("multy")
//                .withProjectDir(new File("src/test/resources/testproject"))
                .build()
        //project.apply plugin: 'net.vlad.eclipse-gradle-nature'
        //project.apply plugin: 'net.vlad.plugin.EclipseGradleNaturePlugin'
        project.apply plugin: 'java'
        project.pluginManager.apply EclipseGradleNaturePlugin
        Project child = ProjectBuilder
                .builder().withName(":subproject1")
                .withParent(project)
                .build()
        child.pluginManager.apply EclipseGradleNaturePlugin
        project.subprojects.add( child )


        project.with {
            allprojects {
                apply plugin: 'java'
            }
            subprojects {
//                hello {
//                    doLast {
//                        println "- hello"
//                    }
//                }
            }

//            project(':subproject1') {
//
//            }

        }
        project.evaluate()


        //assertTrue(project.tasks.hello instanceof GreetingTask)
        assertNotNull project.eclipse.classpath.file
        assertNotNull project.eclipse.project.file
        project.subprojects.each {
            assertNotNull it.eclipse.project.file
        }

//        log.info("file: " + project.eclipse.project.file.name)
        project.tasks['eclipse'].execute()
//        project.tasks['clean'].execute()
    }

}
