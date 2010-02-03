(:
 TC/MD_Q5 Return the headings of the first section of a certain article with id attribute value (9).
:)

for $a in input()/article[@id="9"]
return
    <HeadingOfSection>
        {$a/body/section[1]/@heading}
    </HeadingOfSection>
