(: local database :)
declare variable $blast-gb := fn:collection("/crest/blast-gb.xml")/result;
declare variable $blast-uniprot := fn:collection("/crest/blast-uniprot.xml")/result;
(: remote basebase :)
declare variable $genbank := fn:collection("/crest/genbank.xml");

let $gb-itor := (for $e in $blast-gb/BlastOutput
                let $hit := $e/BlastOutput_iterations/Iteration/Iteration_hits/Hit
                where $hit/Hit_num = 1 and $hit/Hit_hsps/Hsp/Hsp_evalue/text() <= 1.0e-8
                return $e),
    $uniprot-itor := (for $e in $blast-uniprot/BlastOutput
                     let $hit := $e/BlastOutput_iterations/Iteration/Iteration_hits/Hit
                     where $hit/Hit_num = 1 and $hit/Hit_hsps/Hsp/Hsp_evalue/text() <= 1.0e-8
                     return $e)
let $gb_entry := $gb-itor,
    $uniprot_entry := $uniprot-itor
where $gb_entry/BlastOutput_query-def/text() = $uniprot_entry/BlastOutput_query-def/text()
return
  <result>
    {
      let $gb-hit := $gb_entry/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
          $gb-key := $gb-hit/Hit_gb/text()
      return
        let $genbank-remote-entries := $genbank/genbank/INSDSeq[INSDSeq_accession-version/text() = $gb-key]
        for $remote_entry at $pos in $genbank-remote-entries
        let $gb_definition := $remote_entry/INSDSeq_definition,
            $gb_feature := $remote_entry/INSDSeq_feature-table/INSDFeature,
            $blast_evalue := $gb-hit[Hit_gb/text() = $remote_entry/INSDSeq_accession-version/text()]/Hit_hsps/Hsp/Hsp_evalue
        return <genbank num="{ $pos }">{ $blast_evalue, $gb_definition, $gb_feature } </genbank>
    }
  </result>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="built-in" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="saxon" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="101544584" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)