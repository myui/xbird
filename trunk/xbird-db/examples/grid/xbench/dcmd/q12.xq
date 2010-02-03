(:
# DC/MD_Q12 List all order lines of a certain order with id attribute value (5).
:)

declare function local:exec($doc as node()) 
{
	for $a in $doc/order[@id="5"]
	return
	    <Output>
	        {$a/order_lines}
	    </Output>
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}