(:
# DC/MD_Q8 Return the order line item ids of an order with an attribute value (3).
:)

for $a in input()/order[@id="3"]
return
    $a/*/order_line/item_id