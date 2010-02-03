(:
-- Q5.  How many sold items cost more than 40?
:)

let $auction := fn:collection("/vldb/xmark1.xml")
return
  count(for $i in $auction/site/closed_auctions/closed_auction
  where $i/price/text() >= 40
  return $i/price)