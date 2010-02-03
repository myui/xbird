(:
-- Q3. Return the IDs of all open auctions whose current
--     increase is at least twice as high as the initial increase.
:)

let $auction := fn:doc("auction.xml")
return
  for $b in $auction/site/open_auctions/open_auction
  where zero-or-one($b/bidder[1]/increase/text()) * 2 <= $b/bidder[last()]/increase/text()
  return <increase first="{ $b/bidder[1]/increase/text() }" last="{ $b/bidder[last()]/increase/text() }"/>
