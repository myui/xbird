import module namespace view = "http://xbird/view" 
 at "file:///D:/workspace/xbird/main/test/resources/org/metabrick/xbird/xquery/parser/data/view3.xq";

<result>
{
  for $t in 
    fn:distinct-values(view:body()/book/title)
  return
    <booktitle>
      $t/text()
    </booktitle>
}
</result>