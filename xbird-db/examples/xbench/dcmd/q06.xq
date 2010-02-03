(:
# DC/MD_Q6 Return invoice where some discount rates of sub-line items are higher than a certain number (0.02).
:)

for $ord in input()/order
where some $item in $ord/order_lines/order_line
    satisfies $item/discount_rate > 0.02
return
    $ord