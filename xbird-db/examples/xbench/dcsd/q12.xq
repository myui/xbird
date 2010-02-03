(:
 DC/SD_Q12 Get the mailing address of the first author of certain item with id attribute value (I6).
:)

for $a in input()/catalog/item[@id="I6"]
return
    <Output>
        {$a/authors/author[1]/contact_information/mailing_address}
    </Output>
