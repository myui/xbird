(:
 DC/SD_Q3 Group items released in a certain year (1990), by publisher name and calculate the total number of items for each group.
:)

for $a in distinct-values (input()/catalog/item
    [date_of_release >= "1990-01-01"]
    [date_of_release < "1991-01-01"]/publisher/name)
let $b := input()/catalog/item/publisher[name=$a]
return
    <Output>
        <Publisher>{$a}</Publisher>
        <NumberOfItems>{count($b)}</NumberOfItems>
    </Output>
