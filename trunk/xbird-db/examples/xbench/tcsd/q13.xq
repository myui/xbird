(:
 TC/SD_Q13 Construct a brief information on a word (``his"), including: headword, pronunciation, part_of_speech, first etymology and first sense definition.
:)

for $a in input()/dictionary/e
where $a/hwg/hw="his"
return
    <Output>
        {$a/hwg/hw}
        {$a/hwg/pr}
        {$a/hwg/pos}
        {$a/etymology/cr[1]}
        {$a/ss/s[1]/def}
    </Output>
