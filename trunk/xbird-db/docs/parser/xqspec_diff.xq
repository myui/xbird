(: Copyright 2005 Makoto YUI.  All Rights Reserved. :)

declare default element namespace "http://www.w3.org/1999/xhtml";
declare variable $latest_xquery_spec := fn:doc("http://www.w3.org/TR/xquery/");
declare variable $old_xquery_spec := fn:doc("http://www.w3.org/TR/2005/WD-xquery-20050404/");
declare variable $latest_ebnf_table := $latest_xquery_spec//div[@class = "div2"][h3/a/@id = "id-grammar"]/table[@summary = "Scrap"];
declare variable $old_ebnf_table := $old_xquery_spec//div[@class = "div2"][h3/a/@id = "id-grammar"]/table[@summary = "Scrap"];

<html>
  <h3>
  	Latest Spec: <a href="{ $latest_xquery_spec/html/body/div/dl/dd[preceding-sibling::dt[1]/text() = "This version:"]/span/a/text() } ">
                        { $latest_xquery_spec/html/body/div/h2[a/@name = "w3c-doctype"]/text() }</a><br/>
  	Comparing Spec: <a href="{ $old_xquery_spec/html/body/div/dl/dd[preceding-sibling::dt[1]/text() = "This version:"]/span/a/text() } ">
                        { $old_xquery_spec/html/body/div/h2[a/@name = "w3c-doctype"]/text() }</a><br/>
	Generated Date: { fn:current-date() }
</h3>
  <body>
    <table border="1">
      {
        for $line_tr at $pos in $latest_ebnf_table/tbody/tr
        let $old_line_trs := $old_ebnf_table/tbody/tr
        return
          <tbody>
            {
              if(every $outer in $line_tr/td[fn:position() > 1] satisfies some  $inner in $old_line_trs/td[fn:position() > 1][. = $outer] satisfies $inner eq $outer) then $line_tr
              else ($line_tr, <tr bgcolor="gray">{ $old_line_trs[td[1]/a/@id = $line_tr/td[1]/a/@id]/td }</tr>)
            }
          </tbody>
      }
    </table>
  </body>
</html>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="internal" tcpport="1233864" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition><template name="xquery_body"></template></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)