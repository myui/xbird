(:
-- Q8. List the names of persons and the number of items they bought.
--     (joins person, closed\_auction)}
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $p in $auction/site/people/person
	  let $a := for $t in $auction/site/closed_auctions/closed_auction
	            where $t/buyer/@person = $p/@id
	            return $t
	  return <item person="{ $p/name/text() }">{ count($a) }</item>
}