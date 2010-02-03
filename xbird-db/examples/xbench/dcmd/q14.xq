(:
# DC/MD_Q14 List the ids of orders that only have one order line.
:)

for $a in input()/order
where empty($a/order_lines/order_line[2])
return
    <OneItemLine>
        {$a/@id}
    </OneItemLine>