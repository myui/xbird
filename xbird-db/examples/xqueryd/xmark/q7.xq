(:
-- Q7. How many pieces of prose are in our database?}
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $p in $auction/site
	  return count($p//description) + count($p//annotation) + count($p//emailaddress)
}