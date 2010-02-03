(:
# DC/MD_Q10 List the orders (order id, order date and ship type), with total amount larger than a certain number (11000.0), ordered alphabetically by ship type.
:)

declare function local:exec($doc as node()) 
{
	for $a in $doc/order
	where $a/total > 11000.0
	order by fn:zero-or-one($a/ship_type)
	return
	    <Output>
	        {$a/@id}
	        {$a/order_date}
	        {$a/ship_type}
	    </Output>
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}