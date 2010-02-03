(:
#  DC/MD_Q1 Return the customer id of the order that has matching id attribute value (1).
:)


declare function local:exec($doc as node()) 
{
	for $order in $doc/order[@id="1"]
	return
	    $order/customer_id
};
    
let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}
