let $d := collection("/dcmd_n")
return
 (# map $d #) {
	for $doc in $d
	return $doc/order[@id="1"]
 }