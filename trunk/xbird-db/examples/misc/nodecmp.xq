let $arg1 := (<element1>some data 1</element1>,<element2>some data 2</element2>)
return
 for $n at $i in $arg1 where ($n is $arg1[2]) return $i
