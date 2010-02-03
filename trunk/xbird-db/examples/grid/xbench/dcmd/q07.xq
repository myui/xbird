(:
# DC/MD_Q7 Return invoice where all discount rates of sub-line items are higher than a certain number (0.02).
:)

declare function local:exec($doc as node()) 
{
	for $ord in $doc/order
	where every $item in $ord/order_lines/order_line
	    satisfies $item/discount_rate > 0.02
	return
	    $ord
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}
