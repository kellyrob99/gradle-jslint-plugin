package org.kar.jslint.gradle.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.rules.TemporaryFolder
import static org.hamcrest.Matchers.instanceOf
import org.junit.*
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import org.gradle.api.Task
import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration

/**
 * @author Kelly Robinson
 */
class JSLintPluginTest
{
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();
    private Project project
    private final JSLintPlugin plugin = new JSLintPlugin()

    @Before
    public void setup()
    {
        project = ProjectBuilder.builder().withProjectDir(tmpDir.folder).build()
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
        assertThat (tasks.size(), equalTo(1) )
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
}
