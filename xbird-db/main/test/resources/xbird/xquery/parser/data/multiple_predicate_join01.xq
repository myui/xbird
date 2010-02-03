for $a in fn:doc("for_norm.xml")/root/aaa, 
	$b in fn:doc("for_norm.xml")/root/bbb
where
	$a/ccc = $b/ccc and $a/ddd = $b/ddd
	or $a/eee = $b/eee
return
	$a