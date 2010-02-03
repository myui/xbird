(:
 TC/MD_Q16 Get the article by its id attribute value (6).
:)

for $a in input()/article[@id="6"]
return
    $a
