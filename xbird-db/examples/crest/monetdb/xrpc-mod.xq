module namespace foo = "xrpc-test-function";

declare function foo:add($v1 as xs:integer, $v2 as xs:integer) as
xs:integer
{
    $v1 + $v2
};

declare function foo:q1($gb-key as xs:string*) as node()*
{
    fn:doc("genbank.xml")/genbank/INSDSeq[INSDSeq_accession-version/text() = $gb-key]
};
