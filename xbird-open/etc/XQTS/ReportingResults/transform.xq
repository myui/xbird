let $doc := fn:doc("test-reports_wrapped.xml")
let $testcase := $doc/root/test-case
let $tcnames := fn:distinct-values($testcase/@name)
for $tcname in $tcnames
	let $tc := $testcase[@name = $tcname][last()]
	return $tc
