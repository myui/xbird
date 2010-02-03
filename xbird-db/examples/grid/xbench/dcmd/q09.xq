(:
# DC/MD_Q9 Return the item ids of an order with id attribute value (4).
:)
   
declare function local:exec($doc as node()) 
{
	for $a in $doc/order[@id="4"]
	return
	    $a//item_id
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}