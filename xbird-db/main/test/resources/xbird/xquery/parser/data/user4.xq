import module namespace view = "http://xbird/view" 
 at "file:///D:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/view4.xq";

<result>
{	
	for $t in view:body()/book/title 
	return $t
}
</result>
