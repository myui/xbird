declare variable $colname := "/repos/xmark10.xml";

declare variable $remote-endpoint1 := "//xanadu:1099/xbird/srv-01";
declare variable $remote-endpoint2 := "//knuth:1099/xbird/srv-01";
declare variable $remote-endpoint3 := "//uran:1099/xbird/srv-01";

declare function local:select1() {
	execute at $remote-endpoint1 { 
		fn:collection($colname)/site/closed_auctions/closed_auction
	}
};

declare function local:select2() {
	execute at $remote-endpoint2 { 
		fn:collection($colname)/site/open_auctions/open_auction
	}
};

declare function local:reduce() {
	execute at $remote-endpoint3 {
		(fn:subsequence(local:select1(), 1, 1000) | fn:subsequence(local:select2(), 1, 1000))
	}
};

declare function local:filter() {
	for $a in local:reduce()
	where $a/seller/@person >= "person10000" or $a/buyer/@person >= "person10000"
	return $a
};

local:filter()
