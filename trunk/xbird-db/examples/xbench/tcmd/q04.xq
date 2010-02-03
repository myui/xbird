(: TC/MD_Q4 Find the heading of the section following the section entitled 
 ``Introduction" in a certain article with id attribute value (8). :)

for $a in input()/article[@id="8"]/body/section[@heading="introduction"],
    $p in input()/article[@id="8"]/body/section[. >> $a][1]
return
    <HeadingOfSection>
        {$p/@heading}
    </HeadingOfSection>
