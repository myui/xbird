(:
 TC/SD_Q14 List the ids of entries that do not have variant form lists and etymologies.
:)

for $a in input()/dictionary/e
where empty($a/vfl) and empty($a/et)
return
    <NoVFLnET>
        {$a/@id}
    </NoVFLnET>
