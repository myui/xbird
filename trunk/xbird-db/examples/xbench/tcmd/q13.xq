(:
 TC/MD_Q13 Construct a brief information on the article that has a matching id attribute value (5), including title, the name of first author, date and abstract.
:)

for $a in input()/article[@id="5"]
return
    <Output>
        {$a/prolog/title}
        {$a/prolog/authors/author[1]/name}
        {$a/prolog/dateline/date}
        {$a/body/abstract}
    </Output>
