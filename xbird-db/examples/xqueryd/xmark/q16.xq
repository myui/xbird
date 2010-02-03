(:
-- Q16. Return the IDs of those auctions
--      that have one or more keywords in emphasis. (cf. Q15)
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $a in $auction/site/closed_auctions/closed_auction
	  where not(empty($a/annotation/description/parlist/listitem/parlist/listitem/text/emph/keyword/text()))
	  return <person id="{ $a/seller/@person }"/>
}