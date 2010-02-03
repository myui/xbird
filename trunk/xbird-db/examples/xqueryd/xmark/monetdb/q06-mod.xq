module namespace xrpc = "xrpc-test-function";

declare function xrpc:select1($colname as xs:string) as node()* {
	doc($colname)/site/closed_auctions/closed_auction
};

declare function xrpc:select2($colname as xs:string) as node()* {
	doc($colname)/site/open_auctions/open_auction
};

declare function xrpc:reduce($colname as xs:string, $endpoint1 as xs:string, $endpoint2 as xs:string) 
as node()*
{
	(
		fn:subsequence(
			( execute at { $endpoint1 } { xrpc:select1($colname) } ), 1, 1000
		) 
	 | 
		fn:subsequence(
			( execute at { $endpoint2 } { xrpc:select2($colname) } ), 1, 1000
		)	
	)
};
