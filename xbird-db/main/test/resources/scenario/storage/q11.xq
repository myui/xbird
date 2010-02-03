(:
-- Q11. For each person, list the number of items currently on sale whose
--      price does not exceed 0.02% of the person's income.
:)

let $auction := fn:doc("auction.xml")
return
  for $p in $auction/site/people/person
  let $l := for $i in $auction/site/open_auctions/open_auction/initial
            where $p/profile/@income > 5000 * exactly-one($i/text())
            return $i
  return <items name="{ $p/name/text() }">{ count($l) }</items>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="xbird" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="custom" tcpport="1233864" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;C:\workspace\xbird\lib\relaxngDatatype.jar;C:\workspace\xbird\lib\xsdlib.jar;C:\workspace\xbird\lib\optional\args4j-2.0.4.jar;C:\workspace\xbird\target\classes&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/><scenario default="no" name="built-in" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="internal" tcpport="14" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)