(:
# DC/MD_Q14 List the ids of orders that only have one order line.
:)

declare function local:exec($doc as node()) 
{
	for $a in $doc/order
	where empty($a/order_lines/order_line[2])
	return
	    <OneItemLine>
	        {$a/@id}
	    </OneItemLine>
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}