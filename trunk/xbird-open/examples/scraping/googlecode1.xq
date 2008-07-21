<html>
<body>
 <table border="1">
  <tr bgcolor="lightgreen">
    <td>order</td><td>message</td>
  </tr>
{
	let $page := fn:doc("http://code.google.com/p/xbird/wiki/WebScraping")
	for $code at $pos in $page/html/body/div[@id='maincol']/div[@id='wikicontent']/pre
	return 
		<tr>
		  <td>{ $pos }</td>
		  <td>{ fn:data($code) }</td>
		</tr>
}
 </table>
</body>
</html>