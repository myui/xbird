(:
 TC/SD_Q18 List the headwords of entries which contain a given phrase (``the hockey").
:)

for $a in input()/dictionary/e
where contains($a, "the hockey")
return
    $a/hwg/hw
