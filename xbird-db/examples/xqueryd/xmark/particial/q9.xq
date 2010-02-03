(:
-- Q9. List the names of persons and the names of the items they bought
--     in Europe.  (joins person, closed\_auction, item)}
:)

declare variable $remote-endpoint1 := "//xanadu:1099/xbird/srv-01";
declare variable $remote-endpoint2 := "//atom:1099/xbird/srv-01";
declare variable $colname := "/sac2007/xmark1.xml";

let $auction := fn:collection($colname)
return
    for $p in $auction/site/people/person
    let $a := execute at $remote-endpoint1 {
    				let $ca := fn:collection($colname)/site/closed_auctions/closed_auction
    				for $t in $ca
              		where $p/@id = $t/buyer/@person
              		return
                		let $n := 
     						execute at $remote-endpoint2 {
                				let $ei := fn:collection($colname)/site/regions/europe/item
                				for $t2 in $ei
	                          	where $t/itemref/@item = $t2/@id
	                       		return $t2
                       		}
                		return <item>{ $n/name/text() }</item>
    return <person name="{ $p/name/text() }">{ $a }</person>
