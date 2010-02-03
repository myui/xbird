(:
-- Q19. Give an alphabetically ordered list of all
--      items along with their location.
:)

let $auction := fn:doc("auction.xml")
return
  for $b in $auction/site/regions//item
  let $k := $b/name/text()
  order by zero-or-one($b/location) ascending empty greatest
  return <item name="{ $k }">{ $b/location/text() }</item>