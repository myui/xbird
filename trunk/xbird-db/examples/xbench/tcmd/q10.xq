(:
 TC/MD_Q10 List the titles of articles sorted by country.
:)

for $a in input()/article/prolog
order by $a/dateline/country
return
    <Output>
        {$a/title}
        {$a/dateline/country}
    </Output>

