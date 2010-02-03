let $auction1 := doc("auction1.xml"),
    $auction2 := doc("auction2.xml")
for $p in $auction1/site/people/person
return
  for $t in $auction2/site/closed_auctions/closed_auction
  where $t/buyer/@person = $p/@id
  return $t(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios/><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)