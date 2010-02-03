(:
-- Q15. Print the keywords in emphasis in annotations of closed auctions.
:)

let $auction := fn:collection("/vldb/xmark1.xml")
return
  for $a in $auction/site/closed_auctions/closed_auction/annotation/description/parlist/listitem/parlist/listitem/text/emph/keyword/text()
  return <text>{ $a }</text>