let $d := /ddd
for $a in /aaa, $b in /bbb, $c in /ccc 
where $d = 3 and $b = $c	(: changed $b <-> $c :)
return ($a, $b, $c)
