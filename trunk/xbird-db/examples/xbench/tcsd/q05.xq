(:
 TC/SD_Q5 Return the first sense of a matching headword (``that").
:)

for $a in input()/dictionary/e
where $a/hwg/hw="that"
return
    $a/ss/s[1]
