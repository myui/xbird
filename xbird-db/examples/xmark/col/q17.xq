(:
-- Q17. Which persons don't have a homepage?
:)

let $auction := fn:collection("/vldb/xmark1.xml")
return
  for $p in $auction/site/people/person
  where empty($p/homepage/text())
  return <person name="{ $p/name/text() }"/>