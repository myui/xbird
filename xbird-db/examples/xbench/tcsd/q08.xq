(:
 TC/SD_Q8 Return Quotation Text (one element name unknown) of a word (``and").
:)

for $ent in input()/dictionary/e
where $ent/*/hw = "and"
return
    $ent/ss/s/qp/*/qt
