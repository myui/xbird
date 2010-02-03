import module namespace xrpc = "xrpc-test-function"
                at "/usr/share/MonetDB/xrpc/export/q06-mod.xq";

declare variable $colname := "xmark1.xml";
declare variable $remote-endpoint1 := "xanadu.naist.jp";
declare variable $remote-endpoint2 := "knuth.naist.jp";
declare variable $remote-endpoint3 := "uran.naist.jp";

declare function local:filter() {
	for $a in 
		execute at {$remote-endpoint3} { xrpc:reduce($colname, $remote-endpoint1, $remote-endpoint2) }
	where $a/seller/@person >= "person10000" or $a/buyer/@person >= "person10000"
	return $a
};

local:filter()[1]