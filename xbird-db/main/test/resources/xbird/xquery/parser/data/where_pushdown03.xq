let $d := /ddd
for $a in /aaa, $b in /bbb, $c in /ccc 
where $d = 3 and $c = $b    (: `and` expression is optimized (or is not). :)
return ($a, $b, $c)
