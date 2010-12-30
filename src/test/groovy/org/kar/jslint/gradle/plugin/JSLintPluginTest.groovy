/*
 * Copyright (c) 2010.
 * Author Kelly Robinson.
 */

package org.kar.jslint.gradle.plugin

import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.rules.TemporaryFolder
import org.gradle.api.Project
import org.gradle.api.Task
import static org.hamcrest.Matchers.*
import org.junit.*
import static org.junit.Assert.*

/**
 * @author Kelly Robinson
 */
class JSLintPluginTest
{
    private static final String TEST_SOURCE_PATH = new File('.', 'src/test/resources').absolutePath
    private static final String ERROR_JS = 'errorjs.js'
    private static final String PASSING_JS = 'simplejs.js'

    private Project project
    private JSLintPlugin plugin

    @Before
    public void setup()
    {
        project = ProjectBuilder.builder().build()
        plugin = new JSLintPlugin()
    }

    @Test
    public void conventionShouldBeRegisteredOnApply()
    {
        plugin.apply(project)
        assertThat(project.convention.plugins.jslint, instanceOf(JSLintPluginConvention))
    }

    @Test
    public void taskShouldBeAddedOnApply()
    {
        plugin.apply(project)
        Set<Task> tasks = project.getTasksByName(JSLintPlugin.TASK_NAME, false)
        assertThat(tasks.size(), equalTo(1))
    }

    @Test
    public void configurationShouldBeAddedOnApply()
    {
        plugin.apply(project)
        DefaultConfiguration configuration = project.configurations.jslint
        //should be 4 dependencies loaded to satisfy the needs of the ant task
        assertEquals(configuration.files.size(), 4)
    }

    @Test
    public void xslFileShouldBeAvailable()
    {
        plugin.apply(project)
        String filename = plugin.loadXslFile()
        File file = new File(filename)
        assertThat(filename, endsWith(JSLintPlugin.XSL_FILE_DIR + '/' + JSLintPlugin.XSL_FILE))
        assertThat(file.exists(), equalTo(true))
        assertThat(file.size(), greaterThan(0l))
    }

    @Test(expected = TaskExecutionException.class)
    public void antTaskShouldFailOnJOnTestJS()
    {
        plugin.apply(project)
        JSLintPluginConvention convention = plugin.jsLintpluginConvention
        convention.with {
            inputDirs = [TEST_SOURCE_PATH]
        }
        project.getTasksByName(JSLintPlugin.TASK_NAME, false).iterator().next().execute()
    }

    @Test
    public void inclusionShouldWorkToSpecifyFiles()
    {
        plugin.apply(project)
        JSLintPluginConvention convention = plugin.jsLintpluginConvention
        convention.with {
            inputDirs = [TEST_SOURCE_PATH]
            includes = PASSING_JS
        }
        project.getTasksByName(JSLintPlugin.TASK_NAME, false).iterator().next().execute()
        File file = new File(convention.createOutputFileName())
        assertThat(file.exists(), equalTo(true))
        assertThat(file.size(), equalTo(0l))
    }

    @Test
    public void exclusionShouldWorkToSpecifyFiles()
    {
        plugin.apply(project)
        JSLintPluginConvention convention = plugin.jsLintpluginConvention
        convention.with {
            inputDirs = [TEST_SOURCE_PATH]
            excludes = ERROR_JS
        }
        project.getTasksByName(JSLintPlugin.TASK_NAME, false).iterator().next().execute()
        File file = new File(convention.createOutputFileName())
        assertThat(file.exists(), equalTo(true))
        assertThat(file.size(), equalTo(0l))
    }

    @Test
    public void settingHaltOnFailureFalseShouldWork()
    {
        plugin.apply(project)
        JSLintPluginConvention convention = plugin.jsLintpluginConvention
        convention.with {
            inputDirs = [TEST_SOURCE_PATH]
            haltOnFailure = false
        }
        project.getTasksByName(JSLintPlugin.TASK_NAME, false).iterator().next().execute()
        File file = new File(convention.createOutputFileName())
        assertThat(file.exists(), equalTo(true))
        assertThat(file.size(), greaterThan(0l))
    }

    @Test
    public void htmlFormatterTypeShouldTriggerXsltTransform()
    {
        plugin.apply(project)
        JSLintPluginConvention convention = plugin.jsLintpluginConvention
        convention.with {
            inputDirs = [TEST_SOURCE_PATH]
            haltOnFailure = false
            formatterType = JSLintPluginConvention.HTML
        }
        project.getTasksByName(JSLintPlugin.TASK_NAME, false).iterator().next().execute()
        String outputFilename = convention.createOutputFileName()
        File file = new File(outputFilename)
        assertThat(file.exists(), equalTo(true))
        assertThat(file.size(), greaterThan(0l))
        File htmlFile = new File(outputFilename.replace('.xml', '.html'))
        assertThat(htmlFile.exists(), equalTo(true))
        assertThat(htmlFile.size(), greaterThan(0l))
    }

    @Test
    public void shouldWorkOnMultipleInputDirs()
    {
        plugin.apply(project)
        JSLintPluginConvention convention = plugin.jsLintpluginConvention
        convention.with {
            inputDirs = [TEST_SOURCE_PATH, TEST_SOURCE_PATH]
            haltOnFailure = false
        }
        project.getTasksByName(JSLintPlugin.TASK_NAME, false).iterator().next().execute()
        String outputFilename = convention.createOutputFileName()
        File file = new File(outputFilename)
        assertThat(file.exists(), equalTo(true))
        assertThat(file.size(), greaterThan(0l))
    }
}
