(: TC/SD_Q1 Return the entry that has matching headword (``the"). :)

for $ent in input()/dictionary/e
where $ent/hwg/hw="the"
return
    $ent
