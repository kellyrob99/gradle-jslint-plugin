Gradle JSLint Plugin
====================
This is a simple Plugin for Gradle which wraps an existing Ant JSLint task and makes available
it's static analysis capabilities for your Gradle builds.

More information about JSLint can be found at: http://www.jslint.com/lint.html

More about the jslint4java ant task can be found at: http://jslint4java.googlecode.com/svn/docs/1.4/ant.html

The Gradle jslint Task can be configured with the following parameters(types and defaults are shown):

```java

    List<String> inputDirs = ['.']    
    String includes = '**/*.js'
    String excludes = ''
    String formatterType = 'plain'
    String destFilename = 'jslint'
    boolean haltOnFailure = true
    String options = ''    
```

The formatterTypes available are: plain, xml, html

Here's an example which uses most of the options:

```

 jslint {
     inputDirs = ['webapp/js']
     haltOnFailure = false
     excludes = '**/metadata/'
     options = 'rhino'
     formatterType = 'html'
 } 
```

You can use a Github repository to provide version 0.2 of this plugin for your build. Here's a sample configuration
that works with Gradle 1.2
```groovy
    apply plugin: 'java'
    group = 'temp'

    buildscript {
    	repositories{
    		maven{
    			url = 'http://kellyrob99.github.com/Jenkins-api-tour/repository'
    		}
    	}
        dependencies {
            classpath 'org.kar:gradle-jslint-plugin:0.2'
        }
    }

    repositories{
      	mavenCentral() //needed by the plugin to retrieve the jslint jar
    }

    apply plugin: 'jslint'

```

This plugin is made available under the Apache 2.0 license, I hope you find value and have fun with it!
http://www.apache.org/licenses/LICENSE-2.0
