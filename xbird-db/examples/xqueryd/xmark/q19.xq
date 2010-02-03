(:
-- Q19. Give an alphabetically ordered list of all
--      items along with their location.
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $b in $auction/site/regions//item
	  let $k := $b/name/text()
	  order by zero-or-one($b/location) ascending empty greatest
	  return <item name="{ $k }">{ $b/location/text() }</item>
}