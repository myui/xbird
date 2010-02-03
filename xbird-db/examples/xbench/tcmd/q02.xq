(: TC/MD_Q2 Find the title of the article authored by (Ben Yang). :)

for $prolog in input()/article/prolog
where
    $prolog/authors/author/name="Ben Yang"
return
    $prolog/title

