(:
 DC/SD_Q9 Return the ISBN of an item with id attribute value (I5).
:)

for $a in input()/catalog/item
where $a/@id="I5"
return
    $a//ISBN/text()
