<result>{
  for $sales in doc("sales-records.xml")/*/record
  let $storeno := $sales/store-number,
    $product := doc("products.xml")/*/product[name = $sales/product-name],
    $prd := $product,
    $profit := $sales/qty * ($prd/price - $prd/cost)
  group by $storeno
  let $total-store-profit := sum($profit)
  where $total-store-profit > 100
  order by $total-store-profit descending
  return
    <store number = "{$storeno}" total-profit = "{$total-store-profit}"/> 
 }</result>
