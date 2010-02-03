(:
 DC/SD_Q5 Return the information about the first author of item with a matching id attribute value (I3).
:)

for $a in input()/catalog/item[@id="I3"]
return
    $a/authors/author[1]
