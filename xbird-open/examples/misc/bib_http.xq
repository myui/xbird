for $b in fn:doc("http://xbird.googlecode.com/svn/trunk/xbird-open/main/test/resources/example/bib.xml")/bib/book, 
    $t in $b/title
where fn:contains(fn:string($t), "TCP/IP")
return $b