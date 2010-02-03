(:
 TC/MD_Q12 Retrieve the body of the article that has a matching id attribute value (4).
:)

for $a in input()/article[@id="4"]
return
    <Article>
        {$a/body}
    </Article>
