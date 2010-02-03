(:
 DC/SD_Q10 List the item titles ordered alphabetically by publisher name, with release date within a certain time period (from 1990-01-01 to 1995-01-01).
:)

for $a in input()/catalog/item
where zero-or-one($a/date_of_release) gt "1990-01-01" and
    zero-or-one($a/date_of_release) lt "1995-01-01"
order by $a/publisher/name
return
    <Output>
        {$a/title}
        {$a/publisher}
    </Output>
