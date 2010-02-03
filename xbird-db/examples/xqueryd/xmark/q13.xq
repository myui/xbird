(:
-- Q13. List the names of items registered in Australia along with 
--      their descriptions.
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $i in $auction/site/regions/australia/item
	  return <item name="{ $i/name/text() }">{ $i/description }</item>
}