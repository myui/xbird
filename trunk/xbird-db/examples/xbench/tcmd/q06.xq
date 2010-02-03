(:
 TC/MD_Q6 Find titles of articles where both keywords (``the" and ``hockey") are mentioned in the same paragraph of abstracts.
:)

for $a in input()/article
where some $b in $a/body/abstract/p satisfies
    (contains($b, "the") and contains($b, "hockey"))
return
    $a/prolog/title
