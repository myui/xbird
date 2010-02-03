(:
# DC/MD_Q12 List all order lines of a certain order with id attribute value (5).
:)

for $a in input()/order[@id="5"]
return
    <Output>
        {$a/order_lines}
    </Output>