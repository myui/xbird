(:
 TC/MD_Q18 List the titles and abstracts of articles which contain a given phrase (``the hockey").
:)

for $a in input()/article
where contains ($a//p, "the hockey")
return
    <Output>
        {$a/prolog/title}
        {$a/body/abstract}
    </Output>
