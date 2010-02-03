declare function local:one($i as xs:integer) as xs:integer
{
	if ($i = 1) then 1 else local:another($i)
};

declare function local:another($i) as xs:integer 
{
	let $v := $i - 1
	return
		if ($v = 1) then local:one($v) else local:another($v)
};

for $n in 1 to 10
return local:one($n)