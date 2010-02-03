(:
# DC/MD_Q3 Group orders with total amount bigger than a certain number (11000.0), by customer id and calculate the total number of each group.
:)

for $a in distinct-values (input()/order
    [total > 11000.0]/customer_id)
let $b := input()/order[customer_id=$a]
return
    <Output>
        <CustKey>{$a}</CustKey>
        <NumberOfOrders>{count($b)}</NumberOfOrders>
    </Output>
