(: A prefix "file:///" is used to specify a document in file system :)

for $b in fn:doc("file:///C:/Users/myui/workspace/xbird-open/main/test/resources/example/bib.xml")/bib/book, 
    $t in $b/title
where fn:contains(fn:string($t), "TCP/IP")
return $b