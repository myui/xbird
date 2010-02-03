(:
 TC/MD_Q17 Return the titles of articles which contain a certain word (``hockey").
:)

for $a in input()/article
where contains ($a//p, "hockey")
return
    $a/prolog/title
