(:
 TC/SD_Q17 Return the headwords of the entries which contain a certain word (``hockey").
:)

for $a in input()/dictionary/e
where contains ($a, "hockey")
return
    $a/hwg/hw
