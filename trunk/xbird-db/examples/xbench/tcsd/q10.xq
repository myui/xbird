(:
 TC/SD_Q10 List the words and their pronunciation, alphabetically, quoted in a certain year (1900).
:)

for $a in input()/dictionary/e
where $a/ss/s/qp/q/qd = "1900"
order by $a/hwg/hw[1]
return
    <Output>
        {$a/hwg/hw}
        {$a/hwg/pr}
    </Output>
