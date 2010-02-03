(:
# DC/MD_Q3 Group orders with total amount bigger than a certain number (11000.0), by customer id and calculate the total number of each group.
:)

declare function local:exec($doc as node()) 
{
	for $a in distinct-values ($doc/order[total > 11000.0]/customer_id)
	let $b := $doc/order[customer_id=$a]
	return
	    <Output>
	        <CustKey>{$a}</CustKey>
	        <NumberOfOrders>{count($b)}</NumberOfOrders>
	    </Output>
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}


