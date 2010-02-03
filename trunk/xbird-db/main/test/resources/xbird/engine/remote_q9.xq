(:
-- Q9. List the names of persons and the names of the items they bought
--     in Europe.  (joins person, closed\_auction, item)}
:)

declare namespace xbird = "http://metabrick.org/xbird";

let $auction := fn:doc("auction.xml")
return
  let $ca := xbird:remote-eval("//atom:1099/xbird/srv-01", "fn:doc('file:/C:/Software/xmark/auction.xml')/site/closed_auctions/closed_auction")
  return
    let $ei := $auction/site/regions/europe/item
    for $p in $auction/site/people/person
    let $a := for $t in $ca
              where $p/@id = $t/buyer/@person
              return
                let $n := for $t2 in $ei
                          where $t/itemref/@item = $t2/@id
                          return $t2
                return <item>{ $n/name/text() }</item>
    return <person name="{ $p/name/text() }">{ $a }</person>