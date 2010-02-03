(: DC/SD_Q7 Return item information where all its authors are from certain country (Canada). :)

for $item in input()/catalog/item
where every $add in
    $item/authors/author/contact_information/mailing_address
satisfies $add/name_of_country = "Canada"
return
    $item
