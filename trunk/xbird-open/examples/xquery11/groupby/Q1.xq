<sales-qty-by-product>{
  for $sales in doc("sales-records.xml")/*/record
  let $pname := $sales/product-name
  group by $pname
  order by $pname
  return
    <product name="{$pname}">{
      sum($sales/qty)
    }</product>
}</sales-qty-by-product> 
