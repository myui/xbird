(:
# DC/MD_Q4 List the item id of the previous item of a matching item with id attribute value (8).
:)

let $item := input()/items/item[@id="8"]
for $prevItem in input()/items/item[. << fn:zero-or-one($item)][position() = last()]
return
    <Output>
        <CurrentItem>{$item/@id}</CurrentItem>
        <PreviousItem>{$prevItem/@id}</PreviousItem>
    </Output>