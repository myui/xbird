(: The base directory for a relative path is the path to this query file :)

for $b in fn:doc("bib.xml")/bib/book, 
    $t in $b/title
where fn:contains(fn:string($t), "TCP/IP")
return $b