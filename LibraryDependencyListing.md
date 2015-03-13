# Minimum requirements #

  * Java 1.5 or later, preferably from Sun, for JRE. _Compile-time dependency is Java 1.6 or later._
  * lib/xbird-db-xx.jar, lib/commons-logging-xx.jar

# Dependency to open source libraries #

The following libraries are required for building  the distribution package.
The detail of the dependencies are as follows.

| **file path** | **role** | **(main) dependent classes** |  **license** | **included in fat jar?** |
|:--------------|:---------|:-----------------------------|:-------------|:-------------------------|
| lib/xbird-db-xx.jar | Features included in this library consists of unpublished research work. Part of the feature is to be included in the future release of xbird/open. | a bunch of classes |  ASL 2.0 | yes |
| lib/[commons-logging-xx.jar](http://commons.apache.org/logging/) | used for logging functionality. | a bunch of classes | ASL 2.0 | yes |
| lib/optional/[args4j-xx.jar](https://args4j.dev.java.net/) | used for parsing command line arguments. This API is required only when you use XBird by command line. |  [InteractiveShell](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/client/InteractiveShell.java) and [CommandInvoker](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/client/command/CommandInvoker.java) class | MIT | yes |
| lib/optional/[high-scale-lib-xx.jar](http://sourceforge.net/projects/high-scale-lib) | used for its efficiency when many CPUs  ( 8 processors or more)  are available. | classes in xbird-db-xx.jar and classes under [xbird.util.concurrent.xx](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/util/concurrent/). | public domain | yes |
| lib/optional/[icu4j\_xx.jar](http://www.icu-project.org/) | used by [NormalizeUnicode](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/xquery/func/string/NormalizeUnicode.java) class which represents `fn:normalize-unicode()`. | [NormalizeUnicode](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/xquery/func/string/NormalizeUnicode.java) class | [ICU4J](http://xbird.googlecode.com/svn/trunk/xbird-open/lib/license/icu4j-license.html) (BSD like) | no |
| lib/optional/[log4j-xx.jar](http://logging.apache.org/log4j/) | required if you want to log by log4j via `commons-logging` | a bunch of classes which does logging | ASL 2.0 | no |
| lib/optional/[resolver.jar](http://tomcat.apache.org/) | required for resolving XML entities when parsing | [DocumentTableModel](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/xquery/dm/instance/DocumentTableModel.java) class | ASL 1.1 | yes |
| lib/optional/[stax-api-xx.jar](http://stax.codehaus.org/) | required when use stax API. This API is required before JDK 1.6 (JDK 1.6 includes this API.) | [StreamReaderAdapter](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/xquery/dm/adapter/StreamReaderAdapter.java) class |  ASL 2.0 | yes |

## For Compilation ##

| **file path** | **role** | **(main) dependent classes** |  **license** | **included in fat jar?** |
|:--------------|:---------|:-----------------------------|:-------------|:-------------------------|
| lib/optional/[jsr305.jar](http://code.google.com/p/jsr-305/) | required for compilation | in the class with [annotations of software defect detection](http://jcp.org/en/jsr/detail?id=305) | new BSD | no |

## For GUIs ##

| **file path** | **role** | **(main) dependent classes** |  **license** | **included in fat jar?** |
|:--------------|:---------|:-----------------------------|:-------------|:-------------------------|
| lib/optional/[jdic-xx.jar](https://jdic.dev.java.net/), lib/native/jdic | required for system tray functionality | [SysTray](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/tools/SysTray.java) class | LGPL 2.1 | no |

## For XQuery Servlet Pages ##

| **file path** | **role** | **(main) dependent classes** |  **license** | **included in fat jar?** |
|:--------------|:---------|:-----------------------------|:-------------|:-------------------------|
| lib/optional/[servlet-api.jar](http://tomcat.apache.org/) | required for `xqsp` servlet feature. | [XQueryServlet](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/servlet/xqsp/XQueryServlet.java) class | ASL 2.0 | no |

## For Object-XML mapping ##

| **file path** | **role** | **(main) dependent classes** |  **license** | **included in fat jar?** |
|:--------------|:---------|:-----------------------------|:-------------|:-------------------------|
| lib/optional/[xpp3\_min-xx.jar](http://www.extreme.indiana.edu/xgws/xsoap/xpp/mxp1/index.html) | preferably required by [XStream](http://xstream.codehaus.org/tutorial.html). | required by `xstream-xx.jar` | [Indiana University Extreme! Lab Software License ver.1.1.1](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/lib/license/xpp3-LICENSE.txt) (BSD like) | no |
| lib/optional/[xstream-xx.jar](http://xstream.codehaus.org/) | required when using the [XML/Object mapping feature](http://code.google.com/p/xbird/wiki/XmlObjectBinding). |  classes under [xbird.ext.xstream](http://code.google.com/p/xbird/source/browse/#svn/trunk/xbird-open/main/src/java/xbird/ext/xstream) | MIT (new BSD) | no |

## For Screen-scraping ##

| **file path** | **role** | **(main) dependent classes** |  **license** | **included in fat jar?** |
|:--------------|:---------|:-----------------------------|:-------------|:-------------------------|
| lib/optional/[nekohtml.jar](http://sourceforge.net/projects/nekohtml) | required only when you want to handle HTML documents as XML.  |  [HTMLSAXParser](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/util/xml/HTMLSAXParser.java) and [DocumentTableModel](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/xquery/dm/instance/DocumentTableModel.java) class | ASL 2.0 | yes |
| lib/optional/[tagsoup-xx.jar](http://ccil.org/~cowan/XML/tagsoup/) | required only when you want to handle HTML documents as XML. It is not always nessesary because [NekoHTML](http://sourceforge.net/projects/nekohtml) is used by the default setting. | [DocumentTableModel](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/xquery/dm/instance/DocumentTableModel.java) class | ASL 2.0 | no |
| lib/optional/[xercesImpl-xx.jar](http://xerces.apache.org/xerces2-j/) | recommended to use this library for parsing XML documents  because certain version of XML parser in Sun's JDK exhibits strange behavior.  Is also required by cyberneko HTML parser. | [HTMLSAXParser](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/util/xml/HTMLSAXParser.java) | ASL 2.0  | yes |

## For Testing ##

| **file path** | **role** | **(main) dependent classes** |  **license** | **included in fat jar?** |
|:--------------|:---------|:-----------------------------|:-------------|:-------------------------|
| lib/optional/[junit-xx.jar](http://www.junit.org/) | required for test codes | a bunch of classes in [main/test/java](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/test/java/) |  CPL 1.0  | no |
| lib/optional/[parallel-junit.jar](https://parallel-junit.dev.java.net/) | required for test codes | a bunch of classes in [main/test/java](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/test/java/) |  new BSD | no |
| lib/optional/[saxon8-dom.jar](http://sourceforge.net/projects/saxon) | used for cross validation in XQTS tests. | required by `saxon8.jar` | MPL 1.1 | no |
| lib/optional/[saxon8.jar](http://sourceforge.net/projects/saxon) | used for cross validation in XQTS tests. | classes in [main/test/java/xqts](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/test/java/xqts) | MPL 1.1 | no |
| lib/optional/[testng-xx-jdk15.jar](http://testng.org/) | used in (XQTS) test cases. | classes under [main/test/java](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/test/java/xqts) | ASL 2.0 | no |
| lib/optional/[xmlunit-xx.jar](http://xmlunit.sourceforge.net/) | used in (XQTS) test cases. | classes under [main/test/java](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/test/java)  | MIT (new BSD) | no |

## For Experimental (and unrevealed) Feature ##

| **file path** | **role** | **(main) dependent classes** |  **license** | **included in fat jar?** |
|:--------------|:---------|:-----------------------------|:-------------|:-------------------------|
| lib/optional/[mina-core-xx.jar](http://mina.apache.org/) | required for an experimental feature of distributed query processing | in lib/xbird-db-xx.jar | ASL 2.0 | no |
| lib/optional/[mina-integration-beans-xx.jar](http://mina.apache.org/) | required for an experimental feature of distributed query processing (JMX monitoring) | in lib/xbird-db-xx.jar | ASL 2.0 | no |
| lib/optional/[mina-integration-jmx-xx.jar](http://mina.apache.org/) | required for an experimental feature of distributed query processing (JMX monitoring) | in lib/xbird-db-xx.jar | ASL 2.0 | no |
| lib/optional/[mina-integration-ognl-xx.jar](http://mina.apache.org/) | required for an experimental feature of distributed query processing | in lib/xbird-db-xx.jar | ASL 2.0 | no |
| lib/optional/[mina-transport-apr-xx.jar](http://mina.apache.org/) | required for an experimental feature of distributed query processing (Remote paing) | in lib/xbird-db-xx.jar | ASL 2.0 | no |
| lib/optional/[ognl-xx.jar](http://www.opensymphony.com/ognl/) | required by Apache MINA | in lib/optional/mina-integration-ognl-xx.jar | new BSD | no |
| lib/optional/[jgroups-core-xx.jar](http://www.jgroups.org/) | required for a (unrevealed) map-reduce functionality on grid environment | in lib/xbird-db-xx.jar | LGPL 2.1 | no |
| lib/optional/[slf4j-api-xx.jar](http://www.slf4j.org/) | required by Apache MINA | in lib/optional/mina-core-xx.jar | MIT | no |
| lib/optional/[slf4j-log4j12-xx.jar](http://www.slf4j.org/) | required by Apache MINA (when using log4j) | in lib/optional/mina-core-xx.jar | MIT | no |
| lib/optional/[tokyocabinet-xx.jar](http://tokyocabinet.sourceforge.net/index.html) | required for a (unrevealed) DHT functionality on P2P environment | in lib/xbird-db-xx.jar | LGPL 2.1 | no |
| lib/optional/[tomcat-apr-xx.jar](http://tomcat.apache.org/tomcat-5.5-doc/apr.html) | required for an experimental feature of distributed query processing (Remote paing) | in lib/optional/mina-transport-apr-xx.jar | ASL 2.0 | no |


The above libraries are re-distributed [here](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/lib/).