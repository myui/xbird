## Introduction ##

All XML data stored in XBird is organized into a hierarchy of _collections_.

A collection is exactly what its name suggests: it contains any number of XML documents, and can in addition contain its own child collections, thus providing a hierarchy.
_A collection is directly mapped into a directory_ of the underlying file system.

The "root" collection is also called the Database. It is special in that:
  * It has no parent as the name tells.
  * The database root directory is specified through _xbird.database.datadir_ property in [xbird.properties](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/main/conf/xbird/config/xbird.properties). By the default, `java.io.tmpdir'/xbird is used for the directory.

## Configuring a Database ##

The _xbird.database.datadir_ property can be overrided by putting your modified  _xbird.properties_ file on the directory where System.getProperty("user.home") directs.

```
// You can simply ask the system for the “java.io.tmpdir” and "user.home" properties as following:
String tmpDir = System.getProperty("java.io.tmpdir");
String userHome = System.getProperty("user.home");
System.out.println("java.io.tmpdir: [" + tmpDir + "], user.home: [" + userHome + "]");
```

## Importing Documents ##

### Importing documents by command line ###

Importing XML documents into database is easily accomplished by command line scripts:
[command.sh](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/bin/command.sh) for Unix and [command.bat](http://code.google.com/p/xbird/source/browse/trunk/xbird-open/bin/command.bat) for Windows.

These scripts are included in [xbird-xx-src.zip](http://code.google.com/p/xbird/downloads/list). We assume the decompressed directory as $XBIRD\_HOME in this document.

The usage of these scripts is quite simple.
```
[Unix]
# import an XML document into "/test1" collection.
 $ ./command.sh -col test1 import document /pathto/some.xml
# import XML documents in /pathto/folder into the root (i.e., "/") collection.
 $ ./command.sh import document /pathto/folder
# import XML documents *recursively* in the sub-directories of /pathto/folder into the root collection.
 $ ./command.sh import document /pathto/folder recursive

[Windows]
# import an XML document into "/test1" collection.
 $ command.bat -col test1 import document C:\pathto\some.xml
```

The above command creates the following files:
```
xbird.database.datadir              # root collection 
└─test1                                # '/test1' collection
        some.xml.dtms                  # some.xml document
        test1.dtmp                       # properties of collection
        test1.qnames                   # manages qnames of the documents in 'test1' collection
        test1.strc                        # manages texts (textchunks) of the documents in 'test1' collection
        test1.strc.cache              # cache file for text chunks
        test1.strc.di_h                 # index of the string chunks
```

## Deleting documents or collections ##

Just delete directories or _`some.xml.*`_ files in the database directory :-)

## Accessing to documents in collection ##

The fn:collection function returns a sequence of documents in the specified collection.

It takes the syntax:
**[fn:collection($arg as xs:string?) as node()\*](http://www.w3.org/TR/xpath-functions/#func-collection)**

```
# To retrieve the "some.xml" document in "test1" collection.
fn:collection("/test1/some.xml")

# To retrieve all documents in "test1" collection.
fn:collection("/test1")
```

You can pass a regex expression in [Java's regex syntax](http://java.sun.com/docs/books/tutorial/essential/regex/) to $arg.
```
# To retrieve every documents which name start with a prefix 's' and end with '.xml' in the collection 'test1'. 
fn:collection("/test1/s.*.xml")
```