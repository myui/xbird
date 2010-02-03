declare namespace xbird = "http://metabrick.org/xbird";

(: local database :)
declare variable $blast-gb := fn:collection("/crest/blast-gb.xml")/result;
declare variable $blast-uniprot := fn:collection("/crest/blast-uniprot.xml")/result;

(: remote basebase :)
declare variable $remote-endpoint := "//atom:1099/xbird/srv-01";

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
      let $gb-hit := $gb_entries/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
          $gb-key := $gb-hit/Hit_gb/text()
      return
        let $genbank-remote-entries := 
        	xbird:remote-eval($remote-endpoint, 'fn:collection("/crest/genbank.xml")/genbank/INSDSeq[INSDSeq_accession-version/text() = $gb-key]', $gb-key)
        return
          for $remote_entry at $pos in $genbank-remote-entries
          let $quals := $remote_entry/INSDSeq_feature-table/INSDFeature/INSDFeature_quals
          where $quals/INSDQualifier/INSDQualifier_name/text() = "gene"
          return <genbank num="{ $pos }">{ $quals } </genbank>
    }
  </result>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios/><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)