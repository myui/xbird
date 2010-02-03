(:
-- Q8. List the names of persons and the number of items they bought.
--     (joins person, closed\_auction)}
:)

declare variable $remote-endpoint := "//xanadu:1099/xbird/srv-01";
declare variable $colname := "/sac2007/xmark1.xml";

let $auction := fn:collection($colname)
return
  for $p in $auction/site/people/person
  let $a := execute at $remote-endpoint {
  				for $t in fn:collection($colname)/site/closed_auctions/closed_auction
            	where $t/buyer/@person = $p/@id
            	return $t
              }
  return <item person="{ $p/name/text() }">{ count($a) }</item>
