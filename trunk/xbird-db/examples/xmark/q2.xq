(:
-- Q2. Return the initial increases of all open auctions.
:)

let $auction := fn:doc("auction.xml")
return
  for $b in $auction/site/open_auctions/open_auction
  return <increase>{ $b/bidder[1]/increase/text() }</increase>