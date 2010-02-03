(:
 TC/SD_Q11 List the quotation locations and quotation dates, sorted by date, for a word (``word").
:)

for $a in input()/dictionary/e
    [hwg/hw="the"]/ss/s/qp/q
order by $a/qd
return
    <Output>
        {$a/a}
        {$a/qd}
    </Output>
