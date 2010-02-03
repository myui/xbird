(:
-- Q6. How many items are listed on all continents?
:)

let $auction := fn:doc("auction.xml")
return
  for $b in $auction//site/regions
  return count($b//item)