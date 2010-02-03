(:
 TC/SD_Q6 Return the words where some quotations were quoted in a certain year (1900).
:)

for $word in input()/dictionary/e
where some $item in $word/ss/s/qp/q
    satisfies zero-or-one($item/qd) eq "1900"
return
    $word
