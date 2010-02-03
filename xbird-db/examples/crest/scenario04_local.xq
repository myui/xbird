(: local database :)
declare variable $blast-gb := fn:doc("blast-gb.xml")/result;
declare variable $blast-uniprot := fn:doc("blast-uniprot.xml")/result;
(: remote basebase :)
declare variable $genbank := fn:doc("genbank.xml");
declare variable $uniprot := fn:doc("uniprot.xml");

let $gb-output := (for $e in $blast-gb/BlastOutput
                  let $hit := $e/BlastOutput_iterations/Iteration/Iteration_hits/Hit
                  where $hit/Hit_num = 1 and $hit/Hit_hsps/Hsp/Hsp_evalue/text() <= 1.0e-8
                  return $e),
    $uniprot-output := (for $e in $blast-uniprot/BlastOutput
                       let $hit := $e/BlastOutput_iterations/Iteration/Iteration_hits/Hit
                       where $hit/Hit_num = 1 and $hit/Hit_hsps/Hsp/Hsp_evalue/text() <= 1.0e-8
                       return $e)
let $gb_entry := $gb-output,
    $uniprot_entry := $uniprot-output
where $gb_entry/BlastOutput_query-def/text() = $uniprot_entry/BlastOutput_query-def/text()
return
  (: variables to ship :)
  let $gb-hit := $gb_entry/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
      $gb-key := $gb-hit/Hit_gb/text()
  let $uniprot-hit := $uniprot_entry/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
      $uniprot-key := $uniprot-hit/Hit_embl/text()
  (: remote query :)
  let $genbank-remote-entries := $genbank/genbank/INSDSeq[INSDSeq_accession-version/text() = $gb-key],
      $uniprot-remote-entries := $uniprot/uniprot/entry[name/text() = $uniprot-key]
  return
    (: local merge query :)
    let $merged := (for $gb_remote_entry at $gb_pos in $genbank-remote-entries
                   let $gbref := $gb_remote_entry/INSDSeq_references/INSDReference
                   return <seq id="{ $gb_remote_entry/INSDSeq_accession-version/text() }" dbtype="genbank" num="{ $gb_pos }">{ $gbref } </seq>, 
                   for $uniprot_remote_entry at $uni_pos in $uniprot-remote-entries
                   let $uniprot_ref := $uniprot_remote_entry/reference
                   return <seq id="{ $uniprot_remote_entry/name/text() }" dbtype="uniprot" num="{ $uni_pos }">{ $uniprot_ref } </seq>)
    return
      <result>
        {
          for $e in $merged
          order by $e/@id, $e/num
          return $e
        }
      </result>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="custom" tcpport="129" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;D:\workspace\xbird\lib\relaxngDatatype.jar;D:\workspace\xbird\lib\xsdlib.jar;D:\workspace\xbird\lib\optional\args4j-2.0.4.jar;D:\workspace\xbird\target\classes&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)