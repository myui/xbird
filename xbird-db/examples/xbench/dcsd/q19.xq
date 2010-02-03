(:
 DC/SD_Q19 Retrieve the item titles related by certain item with id attribute value (I7).
:)

for $item in input()/catalog/item[@id="I7"],
    $related in input()/catalog/item
where $item/related_items/related_item/item_id = $related/@id
return
    <Output>
        {$related/title}
    </Output>
