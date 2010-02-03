(:
 DC/SD_Q14 Return the names of publishers who publish books between a period of time (from 1990-01-01 to 1991-01-01) but do not have FAX number.
:)

for $a in input()/catalog/item
where zero-or-one($a/date_of_release) gt "1990-01-01" and
           zero-or-one($a/date_of_release) lt "1991-01-01" and
           empty($a/publisher/contact_information/FAX_number)
return
      <Output>
        {$a/publisher/name}
      </Output>
