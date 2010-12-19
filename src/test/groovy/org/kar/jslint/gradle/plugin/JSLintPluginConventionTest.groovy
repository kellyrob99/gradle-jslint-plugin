package org.kar.jslint.gradle.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.*
import org.gradle.api.Project
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.equalTo
import static org.junit.Assert.assertThat
import static org.hamcrest.Matchers.equalTo

/**
 * @author Kelly Robinson
 */
class JSLintPluginConventionTest
{
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();
    private Project project
    private JSLintPluginConvention convention

    @Before
    public void setup()
    {
        project = ProjectBuilder.builder().withProjectDir(tmpDir.folder).build()
        convention = new JSLintPluginConvention(project)
    }

    @Test
    public void conventionDefaultValuesShouldBeSet()
    {
        assertEquals(convention.formatterType, 'plain')
        assertEquals(convention.destFilename, 'jslint')
        assertEquals(convention.haltOnFailure, 'true')
        assertEquals(convention.options, '')
        assertEquals(convention.inputDirs, ['.'])
        assertEquals(convention.includes, '**/*.js')
        assertEquals(convention.excludes, '')
    }

    @Test
    public void outputFileNameShouldBeTestForPlainFormatterType()
    {
        assertEquals("${project.reportsDir}/jslint.txt".toString(), convention.createOutputFileName())
    }

    @Test
    public void outputFileNameShouldBeSameForXmlOrHtmlFormatterType()
    {
        String filename = "${project.reportsDir}/jslint.xml".toString()
        ['xml', 'html'].each { formatterType ->
            convention.formatterType = formatterType
            assertEquals(filename, convention.createOutputFileName())
        }
    }

    @Test
    public void formatterTypeIsTxtForPlain()
    {
        assertEquals(JSLintPluginConvention.TXT, convention.decideFormat())
    }

    @Test
    public void formatterTypeIsXmlIfNotPlain()
    {
         ['xml', 'html'].each { formatterType ->
            convention.formatterType = formatterType
            assertEquals(JSLintPluginConvention.XML, convention.decideFormat())
        }
    }

    @Test
    public void shouldBeConfigurableByClosure()
    {
        String myOptions = 'myOptions1,myOptions2'
        List<String> myInputDirs = ['dir1', 'dir2']
        convention.jslint {
            formatterType = 'xml'
            options = myOptions
            inputDirs = myInputDirs
            haltOnFailure = 'false'
        }
        assertEquals ('xml', convention.formatterType)
        assertEquals (myOptions, convention.options)
        assertEquals (myInputDirs, convention.inputDirs)
        assertEquals ('false', convention.haltOnFailure)
    }

    @Test
    public void taskPropertiesShouldOnlyMapHaltFailureByDefault()
    {
        LinkedHashMap<String, String> properties = convention.mapTaskProperties()
        assertThat(properties.size(), equalTo(1))
        assertThat(properties.haltOnFailure, equalTo(convention.haltOnFailure))
    }

    @Test
    public void taskPropertiesShouldIncludeOptionsIfSpecified()
    {
        String myOptions = 'myOptions'
        convention.haltOnFailure = 'false'
        convention.options = myOptions
        LinkedHashMap<String, String> properties = convention.mapTaskProperties()
        assertThat(properties.size(), equalTo(2))
        assertThat(properties.haltOnFailure, equalTo(convention.haltOnFailure))
        assertThat(properties.options, equalTo(myOptions))
    }
}
