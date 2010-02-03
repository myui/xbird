(:
 TC/MD_Q11 List the titles of articles that have a matching country element type (Canada), sorted by date.
:)

for $a in input()/article/prolog
where $a/dateline/country="Canada"
order by $a/dateline/date
return
    <Output>
        {$a/title}
        {$a/dateline/date}
    </Output>
