(:
# DC/MD_Q11 List the orders (order id, order date and order total), with total amount larger than a certain number (11000.0), in descending order by total amount.
:)

for $a in input()/order
where $a/total gt 11000.0
order by $a/total descending
return
    <Output>
        {$a/@id}
        {$a/order_date}
        {$a/total}
    </Output>