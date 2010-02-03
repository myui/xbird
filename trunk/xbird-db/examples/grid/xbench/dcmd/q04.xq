(:
# DC/MD_Q4 List the item id of the previous item of a matching item with id attribute value (8).
:)

declare function local:exec($doc as node()) 
{
	let $item := $doc/items/item[@id="8"]
	for $prevItem in $doc/items/item[. << fn:zero-or-one($item)][position() = last()]
	return
	    <Output>
	        <CurrentItem>{$item/@id}</CurrentItem>
	        <PreviousItem>{$prevItem/@id}</PreviousItem>
	    </Output>
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}
