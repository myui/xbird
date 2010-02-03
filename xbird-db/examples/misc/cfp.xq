<html>
<head><meta http-equiv="content-type" content="text/html; charset=UTF-8"/></head>
<body>
{
	let $td := fn:doc("http://www.inf.ufrgs.br/~mirella/cfps.html")/html/body/center/table[1]/tr[3]/td
	return ( $td/p[1], $td//table[1] )
}
</body>
</html>