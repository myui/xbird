<result>{
  for $book in doc("books.xml")/*/book
  for $author in $book/author
  group by $author
  order by $author
  return
  <author name="{$author}">{ 
    for $b in $book
    order by $b/title
    return
      <title> {fn:data($b/title)} </title>
  }</author> 
}</result>
