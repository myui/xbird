(:
# DC/MD_19 For a particular order with id attribute value (7), get its customer name and phone, and its order status.
:)
    
declare function local:exec($doc as node()) 
{
	for $order in $doc/order,
	    $cust in $doc/customers/customer
	where $order/customer_id = $cust/@id
	    and $order/@id = "7"
	return
	    <Output>
	        {$order/@id}
	        {$order/order_status}
	        {$cust/first_name}
	        {$cust/last_name}
	        {$cust/phone_number}
	    </Output>
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}