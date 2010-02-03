declare variable $colname := "/sac2008/xmark1.xml";

declare variable $remote-endpoint1 := "//genbank:1099/xbird/srv-01";
declare variable $remote-endpoint2 := "//uniprot:1099/xbird/srv-01";
declare variable $remote-endpoint3 := "//xanadu:1099/xbird/srv-01";

declare variable $closed_auctions := execute at $remote-endpoint1 { 
												fn:collection($colname)/site/closed_auctions/closed_auction
										  };
declare variable $open_auctions := execute at $remote-endpoint2 { 
												fn:collection($colname)/site/open_auctions/open_auction
										};

declare function local:reduce() {
	(fn:subsequence($open_auctions, 1, 1000) | fn:subsequence($closed_auctions, 1, 1000))
};

declare function local:filter() {
	for $a in 
		( 
			execute at $remote-endpoint3 { local:reduce() }
		)
	where $a/seller/@person >= "person10000" or $a/buyer/@person >= "person10000"
	return $a
};

local:filter()

