<result>{
  for $sales in doc("sales-records.xml")/*/record
  let $storeno := $sales/store-number
  group by $storeno
  order by $storeno
  return
    <store number = "{$storeno}">{
      for $s in $sales
      order by xs:int($s/qty) descending
      return
        <product name = "{$s/product-name}" qty = "{$s/qty}"/>
    }</store> 
}</result>
