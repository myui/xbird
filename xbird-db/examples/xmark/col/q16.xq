(:
-- Q16. Return the IDs of those auctions
--      that have one or more keywords in emphasis. (cf. Q15)
:)

let $auction := fn:collection("/vldb/xmark1.xml")
return
  for $a in $auction/site/closed_auctions/closed_auction
  where not(empty($a/annotation/description/parlist/listitem/parlist/listitem/text/emph/keyword/text()))
  return <person id="{ $a/seller/@person }"/>