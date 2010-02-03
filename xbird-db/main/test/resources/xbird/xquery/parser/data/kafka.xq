let $bname := "Kafka on the shore"
return 
  <res>
  {
	  for $d in doc("XMark01.xml")
	  where $d/book = $bname
	  order by $d/text()	  
	  return $d/books/author[1]/text()
  }
  </res>
  