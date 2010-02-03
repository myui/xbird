(: Copyright 2005 Makoto YUI.  All Rights Reserved. :)
declare default element namespace "http://www.w3.org/1999/xhtml";
declare variable $xqft_spec := fn:doc("http://www.w3.org/TR/xquery-full-text/#id-grammar");
declare variable $xqupdate_spec := fn:doc("http://www.w3.org/TR/xqupdate/");
declare variable $xqft_ebnf := $xqft_spec//div[@class = "div1"][h2/a/@id = "id-grammar"]/table[@class = "scrap"];
declare variable $xqupdate_ebnf := $xqupdate_spec//div[@class = "div1"][h2/a/@id = "id-grammar"]/table[@class = "scrap"];

<html>
  <h3>
      	Latest Spec: <a href="{ $xqft_spec/html/body/div/dl/dd[preceding-sibling::dt[1]/text() = "This version:"]/a/text() } ">
                      						XQuery 1.0 and XPath 2.0 Full-Text { $xqft_spec/html/body/div/h2[a/@name = "w3c-doctype"]/text() }</a><br/>
      	Comparing Spec: <a href="{ $xqupdate_spec/html/body/div/dl/dd[preceding-sibling::dt[1]/text() = "This version:"]/a/text() } ">
                         						XQuery Update Facility { $xqupdate_spec/html/body/div/h2[a/@name = "w3c-doctype"]/text() }</a><br/>
    	Generated Date: { fn:current-date() }
  </h3>
  <body>
    <table border="1">
      {
        for $line_tr at $pos in $xqft_ebnf/tbody/tr
        let $old_line_trs := $xqupdate_ebnf/tbody/tr
        return
          <tbody>
            {
              if(every $outer in $line_tr/td[fn:position() > 1] satisfies some  $inner in $old_line_trs/td[fn:position() > 1][. = $outer] satisfies $inner eq $outer) then $line_tr
              else
                (let $old_tr := $old_line_trs[td[1]/a/@id = $line_tr/td[1]/a/@id]
                return
                  if(fn:empty($old_tr/td)) then <tr bgcolor="lightyellow">{ $line_tr/td }</tr>
                  else ($line_tr, <tr bgcolor="gray">{ $old_tr/td }</tr>))
            }
          </tbody>
      }
    </table>
  </body>
</html>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="internal" tcpport="1233864" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)