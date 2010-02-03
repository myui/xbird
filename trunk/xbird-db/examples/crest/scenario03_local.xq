(: local database :)
declare variable $blast-gb := fn:doc("blast-gb.xml")/result;
declare variable $blast-uniprot := fn:doc("blast-uniprot.xml")/result;
(: remote basebase :)
declare variable $uniprot := fn:doc("uniprot.xml");

let $gb_entries := (for $e in $blast-gb/BlastOutput
                   let $hit := $e/BlastOutput_iterations/Iteration/Iteration_hits/Hit
                   where $hit/Hit_num = 1 and $hit/Hit_hsps/Hsp/Hsp_evalue/text() <= 1.0e-8
                   return $e),
    $uniprot_entries := (for $e in $blast-uniprot/BlastOutput
                        let $hit := $e/BlastOutput_iterations/Iteration/Iteration_hits/Hit
                        where $hit/Hit_num = 1 and $hit/Hit_hsps/Hsp/Hsp_evalue/text() <= 1.0e-8
                        return $e)
where $gb_entries/BlastOutput_query-def/text() = $uniprot_entries/BlastOutput_query-def/text()
return
  <result>
    {
      let $uniprot-hit := $uniprot_entries/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
          $uniprot-key := $uniprot-hit/Hit_embl/text()
      return
        for $remote_entry at $pos in $uniprot/uniprot/entry[name/text() = $uniprot-key]/organism
        return <uniprot num="{ $pos }">{ $remote_entry } </uniprot>
    }
  </result>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="custom" tcpport="129" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;D:\workspace\xbird\lib\relaxngDatatype.jar;D:\workspace\xbird\lib\xsdlib.jar;D:\workspace\xbird\lib\optional\args4j-2.0.4.jar;D:\workspace\xbird\target\classes&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)