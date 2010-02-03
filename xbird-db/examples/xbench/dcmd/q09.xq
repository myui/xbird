(:
# DC/MD_Q9 Return the item ids of an order with id attribute value (4).
:)

for $a in input()/order[@id="4"]
return
    $a//item_id