(:
 DC/SD_Q11 List the item titles in descending order by date of release with date of release within a certain time range (from 1990-01-01 to 1995-01-01.
:)

for $a in input()/catalog/item
where zero-or-one($a/date_of_release) gt "1990-01-01" and
    zero-or-one($a/date_of_release) lt "1995-01-01"
order by $a/data_of_release descending
return
    <Output>
        {$a/title}
        {$a/date_of_release}
    </Output>
