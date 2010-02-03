(:
-- Q2. Return the initial increases of all open auctions.
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $b in $auction/site/open_auctions/open_auction
	  return <increase>{ $b/bidder[1]/increase/text() }</increase>
}