declare function local:fib($i as xs:integer) as xs:integer
{
  if($i <= 1) then 1
  else 
  	local:fib($i - 1) + local:fib($i - 2)
};

for $n in 0 to 10
return local:fib($n)