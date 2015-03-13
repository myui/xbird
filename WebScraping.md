## Introduction ##

XQuery can be used effectively as a HTML/XML [web-scraping](http://en.wikipedia.org/wiki/Web_scraping) engine (see #1 for detail).

## Example ##

We provide here an example that extracts the following text messeges in this HTML page by XQuery.

```
Extract me please!
```
```
Me too ;-(
```

[The following query](http://xbird.googlecode.com/svn/trunk/xbird-open/examples/scraping/googlecode1.xq) extracts [the table](http://xbird.googlecode.com/svn/trunk/site/resources/misc/googlecode1.xq.html) as provided below.

```
 <table border="1">
  <tr bgcolor="lightgreen">
    <td>order</td><td>message</td>
  </tr>
 {
	let $page := fn:doc("http://code.google.com/p/xbird/wiki/WebScraping")
	for $code at $pos in $page/html/body/div[@id='maincol']/div[@id='wikicontent']/pre
        where $pos le 2
	return 
		<tr>
		  <td>{ $pos }</td>
		  <td>{ fn:data($code) }</td>
		</tr>
 }
 </table>
```
| order | message |
|:------|:--------|
| 1 | Extract me please! |
| 2 | Me too ;-( |

## Current limitation ##

XBird uses [NekoHTML](http://sourceforge.net/projects/nekohtml) or [TagSoup](http://ccil.org/~cowan/XML/tagsoup/) for the HTML parser in [DocumentTableModel class](http://xbird.googlecode.com/svn/trunk/xbird-open/main/src/java/xbird/xquery/dm/instance/DocumentTableModel.java). These parsers does not handle javascripts.

The HTML parser is configurable through _xbird.util.xml.HTMLSAXParser_ property in [xbird.properties](http://xbird.googlecode.com/svn/trunk/xbird-open/main/conf/xbird/config/xbird.properties).

Javascript-aware HTML parsers such as [Cobra: Java HTML Parser](http://lobobrowser.org/cobra/java-html-parser.jsp) could be a help.

## List of future information ##
  * [Java theory and practice: Screen-scraping with XQuery](http://www.ibm.com/developerworks/xml/library/j-jtp03225.html)<sup>#1</sup>
  * [Web scraping entry in Wikipedia](http://en.wikipedia.org/wiki/Web_scraping)
  * [XPather](http://xpath.alephzarro.com/) -- useful firefox plugin for web scraping