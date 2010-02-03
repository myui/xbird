(:
 TC/MD_Q19 List the names of articles cited by an article with a certain id attribute value (7).
:)

for $a in input()/article[@id='7']/epilog/references/a_id,
    $b in input()/article
where $a = $b/@id
return
    <Output>
        {$b/prolog/title}
    </Output>
