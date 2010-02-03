declare variable $colname := "/vldb/xmark10.xml";

for $a in fn:collection($colname)/site/closed_auctions/closed_auction
where $a/price/text() >= 40
return $a/price