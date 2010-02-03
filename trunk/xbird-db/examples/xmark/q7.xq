(:
-- Q7. How many pieces of prose are in our database?}
:)

let $auction := fn:doc("auction.xml")
return
  for $p in $auction/site
  return count($p//description) + count($p//annotation) + count($p//emailaddress)