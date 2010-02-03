for $a in /aaa, $b in /bbb, $c in /ccc 
where $b = $c
return ($a, $b, $c)

(:  
-- shoud be optimized as following.
for $a in /aaa
return
  for $b in /bbb
  return
    for $c in /ccc
    where $b = $c
    return ($a, $b, $c)
:)
