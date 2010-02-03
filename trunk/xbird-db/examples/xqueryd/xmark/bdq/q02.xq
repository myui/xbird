declare namespace xbird = "http://metabrick.org/xbird";

declare variable $colname := "/sac2008/xmark1.xml";
declare variable $remote-endpoint1 := "//xanadu:1099/xbird/srv-01";
declare variable $remote-endpoint2 := "//xanadu:1099/xbird/srv-01";

declare variable $closed_auctions := execute at $remote-endpoint1 { 
												fn:collection($colname)/site/closed_auctions/closed_auction
										  };
declare variable $open_auctions := execute at $remote-endpoint2 { 
												fn:collection($colname)/site/open_auctions/open_auction
										};

declare function local:list() {
	for $a in xbird:piped-union($open_auctions, $closed_auctions)
	where $a/seller/@person = "person0"
	return $a
};

local:list()

(: fn:subsequence(local:list(), 0, 10) :)