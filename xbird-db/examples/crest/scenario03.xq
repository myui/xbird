declare namespace xbird = "http://metabrick.org/xbird";

(: local database :)
declare variable $blast-gb := fn:collection("/crest/blast-gb.xml")/result;
declare variable $blast-uniprot := fn:doc("blast-uniprot.xml")/result;

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
      let $uniprot-hit := $uniprot_entries/BlastOutput_iterations/Iteration/Iteration_hits/Hit,
          $uniprot-key := $uniprot-hit/Hit_embl/text()
      return
      	let $uniprot-remote-entries := 
      		xbird:remote-eval($remote-endpoint, 'fn:collection("/crest/uniprot.xml")/uniprot/entry[name/text() = $uniprot-key]/organism', $uniprot-key)
        for $remote_entry at $pos in $uniprot-remote-entries
        return <uniprot num="{ $pos }">{ $remote_entry } </uniprot>
    }
  </result>
