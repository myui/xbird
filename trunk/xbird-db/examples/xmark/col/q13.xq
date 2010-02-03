(:
-- Q13. List the names of items registered in Australia along with 
--      their descriptions.
:)

let $auction := fn:collection("/vldb/xmark1.xml")
return
  for $i in $auction/site/regions/australia/item
  return <item name="{ $i/name/text() }">{ $i/description }</item>
