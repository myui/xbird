(:
 DC/SD_Q17 Return the ids of items whose descriptions contain a certain word (``hockey").
:)

for $a in input()/catalog/item
where contains (zero-or-one($a/description), "hockey")
return
<Output>
    {$a/@id}
</Output>
