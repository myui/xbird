(:
-- Q6. How many items are listed on all continents?
:)

let $auction := fn:collection("/vldb/xmark1.xml")
return
  for $b in $auction//site/regions
  return count($b//item)