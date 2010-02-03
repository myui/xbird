(:
 TC/MD_Q9 Return all author names (several consecutive element unknown) of the article with matching id attribute value (3).
:)

for $art in input()/article[@id="3"]
return
    $art//author/name
