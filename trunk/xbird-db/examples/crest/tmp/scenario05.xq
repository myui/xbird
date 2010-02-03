declare namespace xbird = "http://metabrick.org/xbird";

(: local database :)
declare variable $blast-gb := fn:collection("/crest/blast-gb.xml")/result;
declare variable $blast-uniprot := fn:collection("/crest/blast-uniprot.xml")/result;

(: remote basebase :)
declare variable $gb-remote-endpoint := "//xanadu:1099/xbird/srv-01";
declare variable $uniprot-remote-endpoint := "//atom:1099/xbird/srv-01";

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
  let $gb-hit := $gb_entry/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
      $gb-key := $gb-hit/Hit_gb/text()
  let $uniprot-hit := $uniprot_entry/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
      $uniprot-key := $uniprot-hit/Hit_embl/text()
  return
  	(: remote-query :)
    let $genbank-remote-entries := 
    		xbird:remote-eval($gb-remote-endpoint, 'fn:collection("/crest/genbank.xml")/genbank/INSDSeq[INSDSeq_accession-version/text() = $gb-key]', $gb-key),
        $uniprot-remote-entries := 
        	xbird:remote-eval($uniprot-remote-endpoint, 'fn:collection("/crest/uniprot.xml")/uniprot/entry[name/text() = $uniprot-key]', $uniprot-key)
    return
      <result>
        {
          for $gb_blast_hit in $gb-hit,
              $gb_remote_entry at $gpos in $genbank-remote-entries[INSDSeq_accession-version/text() = $gb_blast_hit/Hit_gb/text()]
          let $gn_quals := $gb_remote_entry/INSDSeq_feature-table/INSDFeature/INSDFeature_quals,
              $gb_quals_organizm := $gn_quals/INSDQualifier/INSDQualifier_name/text() = "organizm"
          return
            (<genbank id="{ $gb_remote_entry/INSDSeq_accession-version/text() }" num="{ $gpos }">
               { $gb_blast_hit/Hit_hsps/Hsp/Hsp_evalue, $gb_remote_entry/INSDSeq_definition, $gb_quals_organizm } 
             </genbank>, 
            for $uniprot_blast_hit in $uniprot-hit,
                $uniprot-remote-entry at $upos in $uniprot-remote-entries[name/text() = $uniprot_blast_hit/Hit_embl/text()]
            return
              <uniprot id="{ $uniprot-remote-entry/name/text() }" num="{ $upos }">
                { $uniprot_blast_hit/Hit_hsps/Hsp/Hsp_evalue, $uniprot-remote-entry/protein, $uniprot-remote-entry/organism }
              </uniprot>)
        }
      </result>
