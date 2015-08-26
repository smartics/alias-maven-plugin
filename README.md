Alias Maven Plugin
==========================

<img align="right" src="https://raw.githubusercontent.com/smartics/alias-maven-plugin/master/src/site/resources/images/logos/icon-128x128.png" alt="Project Logo" hspace="5">

Generates alias shell scripts to provide ease of use of commonly used shell commands by a team.

##Overview

This plugin for [Maven] (http://maven.apache.org/) creates alias scripts from a configurations file.

So what problem does this plugin solve? Whenever you type a command in a shell, for instance

<pre>mvn clean install</pre>

you could spare time in simply using an alias like this

<pre>i</pre>

If you are using license headers that depend on generated build properties, you end up with typing

<pre>mvn initialize license:format</pre>

In addition to the number of characters to type, it is also inconvenient to remember the name of the 
plugin and the fact, that the <tt>initialize</tt> phase has to be called before calling the <tt>format</tt> 
Mojo. So an alias comes handy:

<pre>l</pre>

While creating those aliases in your environment is no rocket science, why should we not share our 
aliases in our team? By sharing we need to add some comments so that new team members can figure out what 
aliases exist and what they are good for.

This plugin generates shell scripts that set the aliases you define in an XML file and adds some documentation to it:

1. By typing '<tt>h</tt>' (default key, can be changed), the aliases with their commands are printed.
2. A URL to a team page on the wiki or CMS is printed for further information.
3. A report plugin generates a report with the help on each alias to be referenced.

## Documentation

Please visit

  * the [plugin's homepage] (https://www.smartics.eu/confluence/display/HOMESPACE/Alias+Maven+Plugin) - for more information on how to use this plugin.
  * the [plugin's report site] (https://www.smartics.eu/alias-maven-plugin/) - for reporting information on the plugin.
 
## Related Plugins
* [smartics Alias Config] (https://github.com/smartics/config-smartics-alias) - configuration we use at smartics. Use it as an example to create your own!
