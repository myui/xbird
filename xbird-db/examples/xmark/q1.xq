(:
-- Q1.Return the name of the person with ID `person0'
--    registered in North America.
:)

let $auction := fn:doc("auction.xml")
return
  for $b in $auction/site/people/person[@id = "person0"]
  return $b/name/text()