(: TC/MD_Q1 Return the title of the article that has matching id attribute value (1). :)

for $art in input()/article[@id="1"]
return
    $art/prolog/title
