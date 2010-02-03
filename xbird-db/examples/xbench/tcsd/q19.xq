(:
 TC/SD_Q19 Retrieve the headwords of entries cited, in etymology part, by certain entry with id attribute value (E1).
:)

for $ent in input()/dictionary/e[@id="E1"],
    $related in input()/dictionary/e
where $ent/et/cr = $related/@id
return
    <Output>
        {$related/hwg/hw}
    </Output>
