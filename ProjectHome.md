XBird is a light-weight XQuery processor and database system written in Java.
The light-weight means reasonably fast and embeddable.

## Features ##

XBird introduces the following features:
  * XQuery Processor
  * Native XML Database Engine
  * Embeddable Database Engine
  * [Distributed XQuery Processor](http://code.google.com/p/xbird/wiki/XBirdDistributedQuery)
  * Support for HTML [web page scraping](http://code.google.com/p/xbird/wiki/WebScraping)

XBird is currently optimized for read-oriented workloads. It passes [about 91% of the minimal conformance](http://xbird.googlecode.com/svn/trunk/xbird-open/etc/XQTS/ReportingResults/XQTSReport.html) of XQuery Test Suite.

## Documents ##

  * **User's Guide**
    1. [Quick start](http://code.google.com/p/xbird/wiki/QuickStart)
    1. [Database administration ](http://code.google.com/p/xbird/wiki/DatabaseAdministration)
    1. [HTML/XML web page scraping](http://code.google.com/p/xbird/wiki/WebScraping)
    1. [Library dependency listing](http://code.google.com/p/xbird/wiki/LibraryDependencyListing)
  * **Developer's Guide**
    1. [Running and accessing a database server -- Client/Server API](http://code.google.com/p/xbird/wiki/RunningAndAccessingDatabase)
    1. [Programming with the XBird API -- XQuery processor API](http://code.google.com/p/xbird/wiki/ProgrammingAPI)
    1. [Distributed query processing with XBird/D](http://code.google.com/p/xbird/wiki/XBirdDistributedQuery)
    1. [XML/object binding and object persistence](http://code.google.com/p/xbird/wiki/XmlObjectBinding)

_Still work in progress._ Check [wiki pages](http://code.google.com/p/xbird/w/list) for the progress.

## Benefits ##

  * Scalable to giga bytes.
  * Can handle 1GB XML file with **only 64MB** memory (with its compression-enabled database, not with a file).
  * Horizontal scalability can be ensured by [distirbuted XQuery processing](http://code.google.com/p/xbird/wiki/XBirdDistributedQuery).
  * Non-viral open source XQuery implementation. [MPL](http://www.opensource.org/licenses/mozilla1.1.php)/[LGPL](http://www.gnu.org/copyleft/lesser.html)'d processor restricts a certain embedded use.

## Support ##

  * We will answer end-user questions through [Google Groups](http://groups.google.com/group/xbird-users). _Google Groups has no-email delivery option._
  * Please submit bug reports and feature requests to [Issue Tracking](http://code.google.com/p/xbird/issues/list). You can vote to your desired features by appending stars.
  * Code review through [svn browser](http://code.google.com/p/xbird/source/browse) is also welcome. Anyone also can post comments to the source codes through [this svn browser](http://code.google.com/p/xbird/source/browse/).

## Acknowledgment ##

  * This project is sponsored in part by [Information-technology Promotion Agency (IPA)](http://www.ipa.go.jp/index-e.html), Japan.
  * `YourKit is kindly supporting open source projects with its full-featured Java Profiler.` _`YourKit, LLC is creator of innovative and intelligent tools for profiling Java and .NET applications. Take a look at YourKit's leading software products` [YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and [YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp)._