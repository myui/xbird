declare namespace xbird = "http://metabrick.org/xbird";

<result>
  {
    for $t in fn:distinct-values(xbird:view("view.xq")/book/title)
    return
      <booktitle>
              { $t }
       </booktitle>
  }
</result>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="xbird" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="custom" tcpport="1233864" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;C:\workspace\xbird\lib\relaxngDatatype.jar;C:\workspace\xbird\lib\xsdlib.jar;C:\workspace\xbird\lib\optional\args4j-2.0.4.jar;C:\workspace\xbird\target\classes&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)