(:
 TC/MD_Q14 List article title that doesn't have genre element.
:)

for $a in input()/article/prolog
where empty ($a/genre)
return
    <NoGenre>
        {$a/title}
    </NoGenre>
