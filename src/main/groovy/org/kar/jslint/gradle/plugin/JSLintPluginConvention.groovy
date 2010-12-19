package org.kar.jslint.gradle.plugin

import org.gradle.api.Project

/**
 * @author Kelly Robinson
 */
class JSLintPluginConvention
{
    private static final String TXT = 'txt'
    private static final String XML = 'xml'
    private static final String REPORTS_DIR = 'reportsDir'
    private static final String PLAIN = 'plain'
    private static final String HTML = 'html'

    /* jar reference is left untyped to allow more flexible configuration if needed */
    def antjar = 'com.googlecode.jslint4java:jslint4java-ant:1.4.4'
    List<String> inputDirs = ['.']
    String includes = '**/*.js'
    String excludes = ''
    String taskName = 'com.googlecode.jslint4java.ant.JSLintTask'
    String formatterType = 'plain'
    String destFilename = 'jslint'
    boolean haltOnFailure = true
    String options = ''
    String destDir
    String destFile

    public JSLintPluginConvention(Project project)
    {
        if (!project.hasProperty(REPORTS_DIR))
        {
            project.setProperty(REPORTS_DIR, "${project.buildDir}/reports")
        }
        destDir = "${project.reportsDir}"
        destFile = "$destDir/$destFilename"
    }

    /**
     * Perform custom configuration of the plugin using the provided closure.
     * @param closure
     */
    def jslint(Closure closure)
    {
        closure.delegate = this
        closure()
    }

    /**
     * @return the appropriate file name based on formatterType
     */
    String createOutputFileName()
    {
        switch (formatterType)
        {
            case (PLAIN):
                return createOutputFileName(TXT)
            case ([XML, HTML]):
                return createOutputFileName(XML)
        }
    }

    /**
     * @param extension
     * @return name for the output file appended with the provided extension
     */
    String createOutputFileName(String extension)
    {
        "${destFile}.$extension"
    }

    /**
     * The addition of an html output type requires the ant task to produce xml first so
     * the parameter to ant for formatterType might be different than the one set on the plugin.
     *
     * @return which formatterType should be set on the underlying ant task
     */
    String decideFormat()
    {
        switch (formatterType)
        {
            case (PLAIN):
                return PLAIN
            case ([XML, HTML]):
                return XML
        }
    }

    /**
     * @return map of properties to pass to the jslint ant task
     */
    Map mapTaskProperties()
    {
        Map taskProperties = [haltOnFailure: haltOnFailure]
        if (options)
        {
            taskProperties['options'] = options
        }
        return taskProperties
    }
}
