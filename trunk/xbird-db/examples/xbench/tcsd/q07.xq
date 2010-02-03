(:
 TC/SD_Q7 Return the words where all quotations were quoted in a certain year (1900).
:)

for $word in input()/dictionary/e
where every $item in $word/ss/s/qp/q
    satisfies zero-or-one($item/qd) eq "1900"
return
    $word
