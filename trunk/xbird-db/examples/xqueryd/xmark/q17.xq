(:
-- Q17. Which persons don't have a homepage?
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $p in $auction/site/people/person
	  where empty($p/homepage/text())
	  return <person name="{ $p/name/text() }"/>
}