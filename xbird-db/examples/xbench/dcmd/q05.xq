(:
# DC/MD_Q5 Return the first order line item of a certain order with id attribute value (2).
:)

for $a in input()/order[@id="2"]
return
    $a/order_lines/order_line[1]