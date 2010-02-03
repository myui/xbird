(:
 DC/SD_Q6 Return item information where some authors are from certain country (Canada).
:)

for $item in input()/catalog/item
where some $auth in
    $item/authors/author/contact_information/mailing_address
satisfies $auth/name_of_country = "Canada"
return
    $item
