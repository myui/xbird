(:
# DC/MD_Q17 Return the ids of authors whose biographies contain a certain word (``hockey").
:)

declare function local:exec($doc as node()) 
{
	for $a in $doc/authors/author
	where fn:contains(fn:zero-or-one($a/biography), "hockey")
	return
		<Output>
		    {$a/@id}
		</Output>
};

let $d := fn:collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return
		local:exec($doc)
}