module namespace view = "http://xbird/view";

declare function view:body() {
<prices>
{	
	for $book in fn:doc("dxv.xml")/book/row,
		$prices in fn:doc("dxv.xml")/prices/row
	where $book/bid = $prices/bid
	return
		<book>{ $book/title, $prices/price }</book>
}
</prices>
};