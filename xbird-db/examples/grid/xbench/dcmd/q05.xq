(:
# DC/MD_Q5 Return the first order line item of a certain order with id attribute value (2).
:)

declare function local:exec($doc as node()) 
{
	for $a in $doc/order[@id="2"]
	return
	    $a/order_lines/order_line[1]
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}
 