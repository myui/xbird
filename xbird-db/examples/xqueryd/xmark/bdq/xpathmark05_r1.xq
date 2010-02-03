declare variable $colname := "/dews2008/xmark1.xml";

declare variable $remote-endpoint := "//pluto:1099/xbird/srv-01";

execute at $remote-endpoint 
{
	for $a in fn:collection($colname)/site/closed_auctions/closed_auction
	where $a/price/text() >= 40
	return $a/price
}/parent::node()/child::node()[1]
