(:
 TC/MD_Q8 Return the names of all authors (one element name unknown) of the article with matching id attribute value (2).
:)

for $art in input()/article[@id="2"]
return
    $art/prolog/*/author/name
