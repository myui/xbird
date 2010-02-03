(:
# DC/MD_Q16 Retrieve one whole order document with certain id attribute value (6).
:)

for $a in input()/order[@id="6"]
return
    $a