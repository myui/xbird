(:
 TC/MD_Q7 Find titles of articles where a keyword (``hockey") is mentioned in every paragraph of abstract.
:)

for $a in input()/article
where every $b in $a/body/abstract/p satisfies
    contains($b, "hockey")
return
    $a/prolog/title
