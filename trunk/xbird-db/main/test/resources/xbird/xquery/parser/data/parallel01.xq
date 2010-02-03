for $outer in fn:doc("auction.xml")/site, $middle in fn:doc("auction.xml")/regions, $inner in fn:doc("auction.xml")/auctions
where $outer/name = $middle/asia and $outer/name = $inner/auction
return $inner/auction