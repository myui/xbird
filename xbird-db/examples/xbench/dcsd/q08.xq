(:
 DC/SD_Q8 Return the publisher of an item with id attribute value (I4).
:)

for $a in input()/catalog/*[@id="I4"]
return
    $a/publisher
