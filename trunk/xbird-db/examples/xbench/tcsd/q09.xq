(:
 TC/SD_Q9 Return Quotation Text (several consecutive element names unknown) of a word (``and").
:)

for $ent in input()/dictionary/e
where $ent//hw = "or"
return
    $ent//qt
