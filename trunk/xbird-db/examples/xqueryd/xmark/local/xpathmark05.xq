declare variable $colname := "examples/xmark/auction.xml";

for $a in fn:doc($colname)/site/closed_auctions/closed_auction
where $a/price/text() >= 40
return $a/price