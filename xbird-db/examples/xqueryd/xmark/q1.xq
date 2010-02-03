(:
-- Q1.Return the name of the person with ID `person0'
--    registered in North America.
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $b in $auction/site/people/person[@id = "person0"]
	  return $b/name/text()
}