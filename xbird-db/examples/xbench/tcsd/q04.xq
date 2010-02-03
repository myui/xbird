(:
 TC/SD_Q4 List the headword of the previous entry of a matching headword ("you").
:)

let $ent := input()/dictionary/e[hwg/hw="you"]
for $prevEnt in input()/dictionary/e[1]
return
    <Output>
        <CurrentEntry>{$ent/hwg/hw/text()}</CurrentEntry>
        <PreviousEntry>{$prevEnt/hwg/hw/text()}</PreviousEntry>
    </Output>
