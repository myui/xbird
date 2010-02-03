(:
-- Q5.  How many sold items cost more than 40?
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  count(for $i in $auction/site/closed_auctions/closed_auction
	  where $i/price/text() >= 40
	  return $i/price)
}