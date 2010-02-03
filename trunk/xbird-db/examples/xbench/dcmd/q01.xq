(:
#  DC/MD_Q1 Return the customer id of the order that has matching id attribute value (1).
:)

for $order in input()/order[@id="1"]
return
    $order/customer_id