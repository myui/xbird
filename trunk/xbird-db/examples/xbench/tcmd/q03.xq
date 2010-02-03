(: TC/MD_Q3 Group articles by date and calculate the total number of articles in each group. :)

for $a in distinct-values (input()/article/prolog/dateline/date)
let $b := input()/article/prolog/dateline[date=$a]
return
    <Output>
        <Date>{$a}</Date>
        <NumberOfArticles>{count($b)}</NumberOfArticles>
    </Output>


