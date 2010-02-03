(: TC/SD_Q2 Find the headword of the entry which has matching quotation year (1900). :)

for $ent in input()/dictionary/e
where $ent/ss/s/qp/q/qd="1900"
return
    $ent/hwg/hw
