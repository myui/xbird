(:
# DC/MD_Q17 Return the ids of authors whose biographies contain a certain word (``hockey").
:)

for $a in input()/authors/author
where fn:contains(fn:zero-or-one($a/biography), "hockey")
return
<Output>
    {$a/@id}
</Output>
