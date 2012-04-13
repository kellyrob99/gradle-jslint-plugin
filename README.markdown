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

This plugin is madeavailable under the Apache 2.0 license, I hope you find value and have fun with it!
http://www.apache.org/licenses/LICENSE-2.0
