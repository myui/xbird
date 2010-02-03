(:
# DC/MD_Q8 Return the order line item ids of an order with an attribute value (3).
:)

declare function local:exec($doc as node()) 
{
	for $a in $doc/order[@id="3"]
	return
    	$a/*/order_line/item_id
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}