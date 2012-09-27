/*
 * Copyright (c) 2010.
 * Author Kelly Robinson.
 */

package org.kar.jslint.gradle.plugin

import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.logging.Logger
import org.gradle.api.*
import org.gradle.api.artifacts.Configuration

/**
 * Wrap an ant task for performing static analysis of Javascript files using JSLint(http://www.jslint.com/lint.html).
 * The ant task is detailed at http://jslint4java.googlecode.com/svn/docs/1.4/ant.html
 *
 * By default the plugin will scan for all javascript files under the project directory. This is configurable using the
 * 'inputDirs' property, along with the 'includes' and 'excludes' properties, both of which accept standard
 * ant pattern strings.
 *
 * The 'haltOnFailure' and 'options' attributes will be configured on the underlying ant task.
 * This plugin does not support the following attributes for the ant task: encoding, failureProperty and jslint.
 *
 * @author Kelly Robinson
 */
class JSLintPlugin implements Plugin<Project>
{
    private static final String TASK_NAME = 'jslint'
    private static final String XSL_FILE_DIR = "tmp/jslint"
    private static final String XSL_FILE = 'jslint.xsl'

    private Project project
    private Logger logger
    private JSLintPluginConvention jsLintpluginConvention

    void apply(Project project)
    {
        this.project = project
        this.logger = project.logger
        this.jsLintpluginConvention = new JSLintPluginConvention(project)
        project.convention.plugins.jslint = jsLintpluginConvention

        configureDependencies()
        configureJslintTask()
    }

    /**
     * Add the jslint task to the project.
     */
    private def configureJslintTask()
    {
        project.task(TASK_NAME) << {
            project.file(project.reportsDir).mkdirs()
            logger.info("Running jslint on project ${project.name}")
            def antBuilder = services.get(IsolatedAntBuilder)
            final String xlsFilePath = loadXslFile()
            antBuilder.withClasspath(project.configurations.jslint).execute {
                ant.taskdef(name: TASK_NAME, classname: jsLintpluginConvention.TASK_NAME)
                ant."$TASK_NAME"(jsLintpluginConvention.mapTaskProperties()) {
                    formatter(type: jsLintpluginConvention.decideFormat(), destfile: jsLintpluginConvention.createOutputFileName())
                    jsLintpluginConvention.inputDirs.each { dirName ->
                        fileset(dir: dirName, includes: jsLintpluginConvention.includes, excludes: jsLintpluginConvention.excludes)
                    }
                }
                if (jsLintpluginConvention.formatterType == 'html') {
                    ant.xslt(basedir: jsLintpluginConvention.destDir, destdir: jsLintpluginConvention.destDir, style: xlsFilePath)
                }
            }

        }
    }

    /**
     * The ant xslt task needs a place to load the xsl file from. Copy it to a temp folder
     * and return the path to the file.
     * @return path to xsl file
     */
    private String loadXslFile()
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(XSL_FILE)
        File tmpDir = new File(project.buildDir, XSL_FILE_DIR)
        tmpDir.mkdirs()
        File jsLintXsl = new File(tmpDir, XSL_FILE)
        if(jsLintXsl.exists())
        {
            jsLintXsl.delete()
        }
        jsLintXsl << stream
        return jsLintXsl.absolutePath
    }

    /**
     * Add the dependencies required to load the jslint ant task.
     */
    private void configureDependencies()
    {
        Configuration config = project.configurations.findByName('jslint')
        if(!config){
            project.configurations{
                jslint
            }
        }
        if (project.configurations.jslint.dependencies.empty) {
            project.dependencies {
                jslint(JSLintPluginConvention.ANT_JAR)
            }
        }
    }
}


