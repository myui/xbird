module namespace view = "http://xbird/view";

declare function view:body() {
	<prices>
	{
	  for $book in fn:doc("dxv.xml")/book/row,
	      $store in fn:doc("dxv.xml")/store/row,
	      $prices in fn:doc("dxv.xml")/prices/row
	  where $book/bid = $prices/bid 
	  	and $store/sid = $prices/sid
	  return
	    <book>
	      $book/title,
	      $store/source,
	      $prices/price
	    </book>
	}
	</prices>
};