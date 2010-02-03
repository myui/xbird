declare namespace xbird = "http://metabrick.org/xbird";

(: local database :)
declare variable $blast-gb := fn:collection("/crest/blast-gb.xml")/result;
declare variable $blast-uniprot := fn:collection("/crest/blast-uniprot.xml")/result;

(: remote basebase :)
declare variable $gb-remote-endpoint := "//atom:1099/xbird/srv-01";
declare variable $uniprot-remote-endpoint := "//atom:1099/xbird/srv-01";

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
  let $genbank-remote-entries := 
  		xbird:remote-eval($gb-remote-endpoint, 'fn:collection("/crest/genbank.xml")/genbank/INSDSeq[INSDSeq_accession-version/text() = $gb-key]', $gb-key),
      $uniprot-remote-entries := 
      	xbird:remote-eval($uniprot-remote-endpoint, 'fn:collection("/crest/uniprot.xml")/uniprot/entry[name/text() = $uniprot-key]', $uniprot-key)
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
      </result>
