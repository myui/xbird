(: Copyright (c) 2005. Makoto YUI. All rights reserved. :)
(: A program to extract articles related to declared keywords from SigmodRecord :)
declare variable $sigmod := fn:doc("http://www.sigmod.org/record/xml/SigmodRecord/SigmodRecord.xml");
declare variable $keywords := ("xml", "integration", "views");

<html>
  <body>
    <table border="1">
      <tr bgcolor="lightgreen">
        <td><b>title</b></td><td><b>author</b></td></tr>
      {
        for $article in $sigmod/SigmodRecord/issues/issue/articles/article
        let $title := $article/title
        where some  $x in $keywords satisfies fn:contains($title, $x)
        return
          <tr>
            <td>{ $article/title/text() } </td>
            <td>{
                  for $a at $pos in $article/authors/author/text()
                  return
                    if($pos = 1) then $a
                    else (", ", $a)
                }</td>
          </tr>
      }
    </table>
  </body>
</html>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="internal" tcpport="171" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)