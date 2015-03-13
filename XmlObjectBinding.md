# Introduction #

XBird introduces an object persistence feature. It supports querying capabilities to persistence objects with the full XQuery expressibility.

The feature uses [XStream persistence API](http://xstream.codehaus.org/persistence-tutorial.html) to serialize a Java object into XML instead of a binary format, and then use a native XML database, i.e., XBird, to persist the objects.　
It is easy since XStream avoids tedious configuration through [annotations](http://xstream.codehaus.org/annotations-tutorial.html).

_Essentially, what we provided is [an alternative](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/src/java/xbird/ext/xstream/XBirdCollectionStrategy.java) to [FileStreamStrategy](http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/persistence/FileStreamStrategy.html) in [XStream](http://xstream.codehaus.org/)._

# Requirements #

In addition to [the minimum requirements](http://code.google.com/p/xbird/wiki/LibraryDependencyListing) for xbird/open, the X/O binding feature requires the following libraries in _classpath_ to serialize/deserialize objects:

  * lib/optional/[xstream-xx.jar](http://code.google.com/p/xbird/source/browse/#svn/trunk/xbird-open/lib/optional) - [XStream](http://xstream.codehaus.org/) is a simple library to serialize objects to XML and back again. (required)
  * lib/optional/[xpp3\_min-xx.jar](http://code.google.com/p/xbird/source/browse/#svn/trunk/xbird-open/lib/optional) - [XPP](http://www.extreme.indiana.edu/xgws/xsoap/xpp/mxp1/index.html) is a fast XML pull-parser implementation required by XStream. (recommended)

The easiest way to play X/O binding feature is to use [xbird-open-xx\_fat.jar](http://code.google.com/p/xbird/source/browse/#svn/trunk/xbird-open/target) in which [xstream-xx.jar](http://code.google.com/p/xbird/source/browse/#svn/trunk/xbird-open/lib/optional) is contained.

# Coding Guideline #

### Persisting objects to Database ###

[XStream persistence API](http://xstream.codehaus.org/javadoc/index.html) provides persistent [List](http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/persistence/XmlArrayList.html)/[Map](http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/persistence/XmlMap.html)/[Set](http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/persistence/XmlSet.html) to keep track of persistence data.

Here is an example to persist objects using [XmlArrayList](http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/persistence/XmlArrayList.html):
```
XStream xstream = XBirdCollectionStrategy.getAnnotationProcessableXStreamInstance();
XBirdCollectionStrategy strategy = new XBirdCollectionStrategy(COLLECTION_NAME, xstream);

List author1 = Arrays.asList(new Author("anonymous"));
RendezvousMessage msg1 = new RendezvousMessage(15, author1);
System.out.println(xstream.toXML(msg1));
 
List author2 = Arrays.asList(new Author("makoto"), new Author("leo"), new Author("grun"));
RendezvousMessage msg2 = new RendezvousMessage(15, author2, "firstPart", "secondPart");
System.out.println(xstream.toXML(msg2));
 
List list = new XmlArrayList(strategy);
list.add(msg1);
list.add(msg2);
```
The definition of `Author` class and `RendezvousMessage` class can be found in  [XBirdCollectionStrategyTest class](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/test/java/xbird/ext/xstream/XBirdCollectionStrategyTest.java).

The output will be as follows:
```
# The first element in list is stored as "1.xml" in the specified collection.
<message>
  <author>
    <name>anonymous</name>
  </author>
  <content class="java.util.Arrays$ArrayList">
    <a class="string-array"/>
  </content>
  <created>1217087568161</created>
</message>
```
```
# The second element in list is stored as "2.xml" in the specified collection.
<message>
  <author>
    <name>makoto</name>
  </author>
  <author>
    <name>leo</name>
  </author>
  <author>
    <name>grun</name>
  </author>
  <content class="java.util.Arrays$ArrayList">
    <a class="string-array">
      <string>firstPart</string>
      <string>secondPart</string>
    </a>
  </content>
  <created>1217087568163</created>
</message>
```

### Retrieving objects from Database ###

Querying over persistent documents and retrieving the result is capable as follows:
```
String query1 = "fn:collection('/" + COLLECTION_NAME + "/.*.xml')//author";
XQueryProcessor proc = new XQueryProcessor();
XQueryModule compiled1 = proc.parse(query1);
Sequence items = proc.execute(compiled1);
INodeSequence nodes = ProxyNodeSequence.wrap(items, DynamicContext.DUMMY);

for(DTMElement node : nodes) {
   Object unmarshalled = xstream.unmarshal(new DTMReader(node));
   Author author = (Author) unmarshalled;
   System.out.println("author: " + author.getName());
}
```

As you can see the above example, the partial objects are obtainable in addition to the entire object.

_Note that you can define the serialized XML format through [annotations](http://xstream.codehaus.org/annotations-tutorial.html) in XStream. Hence, you can query it!_

The output of the above example will be as follows:
```
author: makoto
author: leo
author: grun
author: anonymous
```

You can find more examples in [here](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/test/java/xbird/ext/xstream/).

# Limitations #

  * Does not support 'live' persistence for the stored objects. Modified objects are required to explicitly (re)set to the collection. Get details on [XStream mailing list](http://thread.gmane.org/gmane.comp.java.xstream.user/4088/focus=4089).

# FAQ #

  * How XBird persistent feature deals with different versions of classes?

> It depends on XStream serialization/deserialization.
> See [XStream FAQ](http://xstream.codehaus.org/faq.html#Serialization_newer_class_versions) for the further information.

# Reference #

  * [XBirdCollectionStrategyTest class](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/test/java/xbird/ext/xstream/XBirdCollectionStrategyTest.java) - a testcase containing example usages
  * [XStream Persistence API](http://xstream.codehaus.org/persistence-tutorial.html) - tutorial with several examples
  * [XStream + Xindice](http://www.jroller.com/rickard/date/20041218) - a similar attempt to ours
  * [Explanation in Japanese](http://db-www.naist.jp/~makoto-y/tdiary/?date=20080727#p01) - a translation of this page in Japanese