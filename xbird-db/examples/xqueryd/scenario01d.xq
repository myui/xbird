declare namespace xbird = "http://metabrick.org/xbird";

(: local database :)
declare variable $blast-gb := fn:collection("/crest/blast-gb.xml")/result;
declare variable $blast-uniprot := fn:collection("/crest/blast-uniprot.xml")/result;

(: remote basebase :)
declare variable $remote-endpoint := "//genbank:1099/xbird/srv-01";

let $gb_entry := (for $e in $blast-gb/BlastOutput
                let $hit := $e/BlastOutput_iterations/Iteration/Iteration_hits/Hit
                where $hit/Hit_num = 1 and $hit/Hit_hsps/Hsp/Hsp_evalue/text() <= 1.0e-8
                return $e),
    $uniprot_entry := (for $e in $blast-uniprot/BlastOutput
                     let $hit := $e/BlastOutput_iterations/Iteration/Iteration_hits/Hit
                     where $hit/Hit_num = 1 and $hit/Hit_hsps/Hsp/Hsp_evalue/text() <= 1.0e-8
                     return $e)
where $gb_entry/BlastOutput_query-def/text() = $uniprot_entry/BlastOutput_query-def/text()
return
  <result>
    {
      let $gb-hit := $gb_entry/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
          $gb-key := $gb-hit/Hit_gb/text()
      return
         let $genbank-remote-entries :=  
      	   ( 
      	     execute at $remote-endpoint {
      	 		fn:collection("/crest/genbank.xml")/genbank/INSDSeq[INSDSeq_accession-version/text() = $gb-key]
      	      }
      	   )
        for $remote_entry at $pos in $genbank-remote-entries
        let $gb_definition := $remote_entry/INSDSeq_definition,
            $gb_feature := $remote_entry/INSDSeq_feature-table/INSDFeature,
            $blast_evalue := $gb-hit[Hit_gb/text() = $remote_entry/INSDSeq_accession-version/text()]/Hit_hsps/Hsp/Hsp_evalue
        return <genbank num="{ $pos }">{ $blast_evalue, $gb_definition, $gb_feature } </genbank>
    }
  </result>