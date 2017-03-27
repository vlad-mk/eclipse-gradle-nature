package net.vlad.plugin

import groovy.util.logging.Log
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.plugins.ide.eclipse.model.SourceFolder
import org.gradle.plugins.ide.internal.IdePlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.eclipse.model.Classpath
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject


/** add gradle nature to eclipse project */
@Log
class EclipseGradleNaturePlugin extends IdePlugin {
	static final connectionDir = 'connection.project.dir'
    static final projectPath = 'project.path'

    private Instantiator instantiator
    private final GradlePropDir  = '.settings/'
    private final GradleProps = [
		'org.eclipse.buildship.core.prefs': [
            'build.commands': 'org.eclipse.jdt.core.javabuilder',
            'connection.arguments': '',
            'connection.gradle.distribution': 'GRADLE_DISTRIBUTION(WRAPPER)',
            'connection.java.home': 'null',
            'connection.jvm.arguments': '',
            //root
            'connection.project.dir': '',
            //subproject, root dir
            //'connection.project.dir': '..',
            'derived.resources': '.gradle,build',
            'eclipse.preferences.version': '1',
            'natures': 'org.eclipse.jdt.core.javanature',
            //project name
            'project.path': ':'
            //subproject
            //'project.path': '\\:com.bhfbank.edge.core'
        ]
    ]
	
    @Inject
    EclipseWtpPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }
	
    @Override protected String getLifecycleTaskName() {
        return 'eclipseGradle';
    }
	
    @Override
    void onApply(Project project) {
        /*
        project.afterEvaluate {
        project.configure {
        if (!project.plugins.hasPlugin('eclipse')) {
        apply plugin: 'eclipse'
        }
        }
        }
         */
 
        EclipsePlugin delegatePlugin = project.getPlugins().apply(EclipsePlugin.class);
        lifecycleTask.description = 'Generates Eclipse Gradle configuration files.'
        cleanTask.description = 'Cleans Eclipse Gradle configuration files.'

        delegatePlugin.getLifecycleTask().dependsOn(getLifecycleTask())
        delegatePlugin.getCleanTask().dependsOn(getCleanTask())
        configureEclipsePlugin(project)
    }
	
	
    private void configureEclipsePlugin(Project project) {

        project.plugins.withType(JavaBasePlugin) {

            project.eclipse.classpath.containers "org.eclipse.buildship.core.gradleclasspathcontainer"

            if (project.eclipse.classpath.file) {
                project.eclipse.classpath.file.whenMerged { Classpath classpath ->
                    classpath.entries.removeAll { entry -> entry.kind == 'lib' }
                    classpath.entries.each { entry ->
                        if(entry.kind == 'src' && entry instanceof SourceFolder) {
                            /*
                                <classpathentry kind="src" path="src/test/java">
                                    <attributes>
                                        <attribute name="FROM_GRADLE_MODEL" value="true"/>
                                    </attributes>
                                </classpathentry>
                            */
                            entry.entryAttributes.put('FROM_GRADLE_MODEL', 'true')
                            log.info("++++++++ entry ++++  " + entry)
                        }
                    }
                }
            }
        }
        project.eclipse.project { p ->
            //project.natures.clear()
            p.natures 'org.eclipse.buildship.core.gradleprojectnature'/*, 'org.eclipse.jdt.core.javanature'*/
            p.buildCommand 'org.eclipse.buildship.core.gradleprojectbuilder'
            if(project == project.rootProject) {
                project.subprojects.each { sp ->
                    p.referencedProjects sp.name
                }
            }
        }
        configureTask(project)
    }

    private void configureTask(Project project) {
        def task = maybeAddTask(project, this, "eclipseGradleProps", DefaultTask.class) {
            description = 'Generate gradle project properties'
            GradleProps.keySet().each { k ->
                outputs.file project.file(GradlePropDir + k)
            }

            GradleProps.values().each { map ->
                //project.getRootDir();
                def root = project.getRootProject().name
                def subprj  =  project.name
                if (root != subprj) {
                    log.info("configure subproject " + subprj)
                    if(map.containsKey(projectPath))
                        map[projectPath] =  map[projectPath] + project.name
                    if(map.containsKey(connectionDir))
                        map[connectionDir] = map[connectionDir] + project.relativePath(project.rootProject.getRootDir())
                }
                log.info( "map: " + map)
            }
			
            outputs.dir project.file(GradlePropDir)
        }

        task.doLast {
            log.info("write grade props")
            writeGradleProps(it)
        }
    }
	
    private Task maybeAddTask(Project project, IdePlugin plugin, String taskName, Class taskType, Closure action) {
        if (project.tasks.findByName(taskName)) { return }
        def task = project.tasks.create(taskName, taskType)
        project.configure(task, action)
        plugin.addWorker(task)
		
        return task;
    }

    private void writeProps(file, map) {
        def props = new Properties()
	
        file.parentFile.with { if(!exists()) mkdirs() }
	  
        file.with {
            if(exists()) withInputStream { stream -> props.load(stream) }
        }
	
        def comments = map['#comments']
        map.remove '#comments'
        //map.each { k, v -> props.setProperty(k, v) }
        props << map
	
        file.withOutputStream {
            stream -> props.store(stream, comments)
        }
    }
	
    private void writeGradleProps(Task task) {
        task.outputs.getFiles().each { file ->
            log.info("taske file: " + file.name)
            if(GradleProps.containsKey(file.name) )
            writeProps(file, GradleProps[file.name])
        }
        //GradleProps.each {k,v -> writeProps(task, k,v)}
    }
		
}