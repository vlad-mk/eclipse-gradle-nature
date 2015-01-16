package net.vlad.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.plugins.ide.internal.IdePlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.eclipse.model.Classpath
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject


/** add gradle nature to eclipse project */
class EclipseGradleNaturePlugin extends IdePlugin {
	
    private Instantiator instantiator
    private final GradlePropDir  = '.settings/gradle/'
    private final GradleProps = [
		'org.springsource.ide.eclipse.gradle.core.import.prefs': [
			'#comments': 'org.springsource.ide.eclipse.gradle.core.preferences.GradleImportPreferences',
			'addResourceFilters':'true',
			'afterTasks':'afterEclipseImport;',
			'beforeTasks':'cleanEclipse;eclipse;',
			'enableAfterTasks':'true',
			'enableBeforeTasks':'true',
			'enableDSLD':'false',
			'enableDependendencyManagement':'true',
			'projects':';'
        ],
		'org.springsource.ide.eclipse.gradle.core.prefs' : [
			'#comments': 'org.springsource.ide.eclipse.gradle.core.preferences.GradleProjectPreferences',
			'org.springsource.ide.eclipse.gradle.linkedresources':'',
			'org.springsource.ide.eclipse.gradle.rootprojectloc':''
        ],
		'org.springsource.ide.eclipse.gradle.refresh.prefs' : [
			'#comments': 'org.springsource.ide.eclipse.gradle.core.actions.GradleRefreshPreferences',
			'addResourceFilters':'true',
			'afterTasks':'afterEclipseImport;',
			'beforeTasks':'cleanEclipse;eclipse;',
			'enableAfterTasks':'true',
			'enableBeforeTasks':'true',
			'enableDSLD':'false',
			'useHierarchicalNames':'false'
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
            
            project.eclipse.classpath.containers "org.springsource.ide.eclipse.gradle.classpathcontainer"

            if(project.eclipse.classpath.file) {
                project.eclipse.classpath.file.whenMerged { Classpath classpath ->
                    classpath.entries.removeAll { entry -> entry.kind == 'lib' }
                }
            } 
            project.eclipse.project {
                natures 'org.springsource.ide.eclipse.gradle.core.nature'/*, 'org.eclipse.jdt.core.javanature'*/
            }
        }
        configureTask(project)
    }

    private void configureTask(Project project) {
        maybeAddTask(project, this, "eclipseGradleProps", DefaultTask.class) {
            description = 'Generate gradle project properties'	
            GradleProps.keySet().each { k -> outputs.dir project.file(GradlePropDir + k)}
			
            outputs.dir project.file(GradlePropDir)
        } << {
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
            if(GradleProps.containsKey(file.name) )
            writeProps(file, GradleProps[file.name])
        }
        //GradleProps.each {k,v -> writeProps(task, k,v)}
    }
		
}