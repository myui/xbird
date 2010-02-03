(:
-- Q10. List all persons according to their interest;
--      use French markup in the result.
:)

declare variable $colname := "/vldb/xmark1.xml";
declare variable $remote-endpoint := "//niagara:1099/xbird/srv-01";

execute at $remote-endpoint
{
	let $auction := fn:collection($colname)
	return
	  for $i in distinct-values($auction/site/people/person/profile/interest/@category)
	  let $p := for $t in $auction/site/people/person
	            where $t/profile/interest/@category = $i
	            return
	              <personne>
	                <statistiques>
	                  <sexe>{ $t/profile/gender/text() }</sexe>
	                  <age>{ $t/profile/age/text() }</age>
	                  <education>{ $t/profile/education/text() }</education>
	                  <revenu>{ fn:data($t/profile/@income) }</revenu>
	                </statistiques>
	                <coordonnees>
	                  <nom>{ $t/name/text() }</nom>
	                  <rue>{ $t/address/street/text() }</rue>
	                  <ville>{ $t/address/city/text() }</ville>
	                  <pays>{ $t/address/country/text() }</pays>
	                  <reseau>
	                    <courrier>{ $t/emailaddress/text() }</courrier>
	                    <pagePerso>{ $t/homepage/text() }</pagePerso>
	                  </reseau>
	                </coordonnees>
	                <cartePaiement>{ $t/creditcard/text() }</cartePaiement>
	              </personne>
	  return <categorie>{ <id>{ $i }</id>, $p }</categorie>
}