(:
-- Q4. List the reserves of those open auctions where a
--     certain person issued a bid before another person.
:)

let $auction := fn:doc("auction.xml")
return
  for $b in $auction/site/open_auctions/open_auction
  where some  $pr1 in $b/bidder/personref[@person = "person20"],
              $pr2 in $b/bidder/personref[@person = "person51"] satisfies $pr1 << $pr2
  return <history>{ $b/reserve/text() }</history>