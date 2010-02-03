(:
 TC/MD_Q15 List author names whose contact elements are empty in articles.
:)

for $a in input()/article/prolog/authors/author
where empty($a/contact/text())
return
    <NoContact>
        {$a/name}
    </NoContact>
