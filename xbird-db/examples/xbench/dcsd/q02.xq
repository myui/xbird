(:
 DC/SD_Q2 Find the title of the item which has matching author first name (Ben).
:)

for $item in input()/catalog/item
where $item/authors/author/name/first_name = "Ben"
return
    $item/title
