(:
# DC/MD_Q11 List the orders (order id, order date and order total), with total amount larger than a certain number (11000.0), in descending order by total amount.
:)
    
declare function local:exec($doc as node()) 
{
	for $a in $doc/order
	where $a/total gt 11000.0
	order by $a/total descending
	return
	    <Output>
	        {$a/@id}
	        {$a/order_date}
	        {$a/total}
	    </Output>
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}