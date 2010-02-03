(:
-- Q14. Return the names of all items whose description contains the 
--      word `gold'.
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $i in $auction/site//item
	  where contains(string(exactly-one($i/description)), "gold")
	  return $i/name/text()
}