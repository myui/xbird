(:
# DC/MD_Q16 Retrieve one whole order document with certain id attribute value (6).
:)

declare function local:exec($doc as node()) 
{
	for $a in $doc/order[@id="6"]
	return
	    $a
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}