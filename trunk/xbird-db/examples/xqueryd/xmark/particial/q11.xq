(:
-- Q11. For each person, list the number of items currently on sale whose
--      price does not exceed 0.02% of the person's income.
:)

declare variable $remote-endpoint := "//xanadu:1099/xbird/srv-01";
declare variable $colname := "/sac2007/xmark1.xml";

let $auction := fn:collection($colname)
return
  for $p in $auction/site/people/person
  let $l := execute at $remote-endpoint {
  				for $i in fn:collection($colname)/site/open_auctions/open_auction/initial
            	where $p/profile/@income > 5000 * exactly-one($i/text())
            	return $i
           	}
  return <items name="{ $p/name/text() }">{ count($l) }</items>
