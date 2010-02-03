(:
 DC/SD_Q4 List the item id of the previous item of a matching item with id attribute value (I2).
:)

let $item := input()/catalog/item[@id="I2"]
for  $prevItem in input()/catalog/item[1]
return
    <Output>
        <CurrentItem>{$item/@id}</CurrentItem>
        <PreviousItem>{$prevItem/@id}</PreviousItem>
    </Output>
