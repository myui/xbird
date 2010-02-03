(:
-- Q14. Return the names of all items whose description contains the 
--      word `gold'.
:)

let $auction := fn:collection("/vldb/xmark1.xml")
return
  for $i in $auction/site//item
  where contains(string(exactly-one($i/description)), "gold")
  return $i/name/text()