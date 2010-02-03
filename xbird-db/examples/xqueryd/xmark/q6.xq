(:
-- Q6. How many items are listed on all continents?
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $b in $auction//site/regions
	  return count($b//item)
}