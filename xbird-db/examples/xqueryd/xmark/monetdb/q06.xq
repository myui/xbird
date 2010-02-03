import module namespace xrpc = "xrpc-test-function"
                at "/usr/share/MonetDB/xrpc/export/q06-mod.xq";

declare variable $colname := "xmark10.xml";
declare variable $remote-endpoint1 := "xanadu.naist.jp";
declare variable $remote-endpoint2 := "knuth.naist.jp";
declare variable $remote-endpoint3 := "uran.naist.jp";

declare function local:filter() {
	for $a in 
		execute at {$remote-endpoint1} { xrpc:reduce($colname, $remote-endpoint2, $remote-endpoint3) }
	where $a/seller/@person >= "person10000" or $a/buyer/@person >= "person10000"
	return $a
};

local:filter()