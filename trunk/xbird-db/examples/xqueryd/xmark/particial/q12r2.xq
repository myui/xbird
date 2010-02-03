(:
-- Q12.  For each richer-than-average person, list the number of items 
--       currently on sale whose price does not exceed 0.02% of the 
--       person's income.
:)

declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";
declare variable $colname := "/dews2008/xmark1.xml";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $p in $auction/site/people/person
	  let $l :=   for $i in fn:collection($colname)/site/open_auctions/open_auction/initial
	            	where $p/profile/@income > 5000 * exactly-one($i/text())
	            	return $i
	  where $p/profile/@income > 50000
	  return $p
}