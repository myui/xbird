(:
 TC/SD_Q3 Group entries by quotation location in a certain quotation year (1900) and calculate the total number entries in each group.
:)

for $a in fn:distinct-values(input()/dictionary/e/ss/s/qp/q[qd="1900"]/loc)
let $b := input()/dictionary/e/ss/s/qp/q[loc=$a]
return
    <Output>
        <Location>{$a}</Location>
        <NumberOfEntries>{count($b)}</NumberOfEntries>
    </Output>
