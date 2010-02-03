(:
-- Q20. Group customers by their
--      income and output the cardinality of each group.
:)

let $auction := fn:collection("/vldb/xmark1.xml")
return
  <result>
    <preferred>
      { count($auction/site/people/person/profile[@income >= 100000]) }
    </preferred>
    <standard>
      { count($auction/site/people/person/profile[@income < 100000 and @income >= 30000]) }
    </standard>
    <challenge>
      { count($auction/site/people/person/profile[@income < 30000]) }
    </challenge>
    <na>
      {
        count(for $p in $auction/site/people/person
        where empty($p/profile/@income)
        return $p)
      }
    </na>
  </result>