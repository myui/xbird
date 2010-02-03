declare variable $colname := "/vldb/xmark1.xml";

declare variable $remote-endpoint := "//isa7-dhcp-38-132:1099/xbird/srv-01";

execute at $remote-endpoint 
{
	for $a in fn:collection($colname)/site/closed_auctions/closed_auction
	where $a/price/text() >= 40
	return $a/price
}
