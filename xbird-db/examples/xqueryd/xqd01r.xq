for $x in fn:doc("X.xml")/a/b[@c="123"],
    $y in ( execute at "http://Y.com" {
				for $h in fn:doc("Y.xml")/d/e[@f=$x/@g]/h
				return $h
			}
	       )
return
	<answer>
		<x> { $x/u } </x>
		<y> { $y/v } </y>
	</answer>
