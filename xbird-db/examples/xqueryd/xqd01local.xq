for $x in fn:doc("X.xml")/a/b[@c="123"],
    $y in (
				for $z in fn:doc("Y.xml")/d/e[@f=$x/@g]
				return $z
	       )/h
return
	<answer>
		<x> { $x/u } </x>
		<y> { $y/v } </y>
	</answer>
