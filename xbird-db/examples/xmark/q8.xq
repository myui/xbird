(:
-- Q8. List the names of persons and the number of items they bought.
--     (joins person, closed\_auction)}
:)

let $auction := doc("auction.xml")
return
  for $p in $auction/site/people/person
  let $a := for $t in $auction/site/closed_auctions/closed_auction
            where $t/buyer/@person = $p/@id
            return $t
  return <item person="{ $p/name/text() }">{ count($a) }</item>
