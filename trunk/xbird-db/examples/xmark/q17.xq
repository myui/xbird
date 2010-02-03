(:
-- Q17. Which persons don't have a homepage?
:)

let $auction := fn:doc("auction.xml")
return
  for $p in $auction/site/people/person
  where empty($p/homepage/text())
  return <person name="{ $p/name/text() }"/>