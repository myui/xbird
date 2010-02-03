(:
  DC/SD_Q1 Return the item that has matching item id attribute value (I1).
:)

for $item in input()/catalog/item[@id="I1"]
return
    $item
