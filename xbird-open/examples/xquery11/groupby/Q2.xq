<result>{   
  for $sales in doc("sales-records.xml")/*/record
  let $state := doc("stores.xml")/*/store[store-number = $sales/store-number]/state
  let $category := doc("products.xml")/*/product[name = $sales/product-name]/category
  group by $state, $category
  order by $state, $category
  return
    <group>
      {$state, $category}
      <total-qty>{sum($sales/qty)}</total-qty>
    </group>
}</result>
