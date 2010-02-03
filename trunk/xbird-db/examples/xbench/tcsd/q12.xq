(: TC/SD_Q12 Retrieve the senses of a word (``his"). :)

for $a in input()/dictionary/e
where $a/hwg/hw="his"
return
    <Entry>
        {$a/ss}
    </Entry>
