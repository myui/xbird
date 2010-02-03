(:
  -- Qizx
 $ java -Xmx255m -jar "D:\workspace\qizxopen-1.0\lib\qizxopen.jar" -out %3 "D:\workspace\xbird\main\test\resources\org\metabrick\xbird\xquery\parser\data\every01.xq"
:)

(:
for $x in (1, 2, 3), $y in (2, 3, 4) 
return <p>"{$x} + {$y} = 4"</p>
:)

every $x in (1, 2, 3), $y in (2, 3, 4) 
     satisfies $x + $y = 4
(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="built-in" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="internal" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/><scenario default="no" name="xbird" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="custom" tcpport="14" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;D:\workspace\xbird\lib\relaxngDatatype.jar;D:\workspace\xbird\lib\xsdlib.jar;D:\workspace\xbird\lib\optional\args4j-2.0.4.jar;D:\workspace\xbird\target\classes&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="83886080" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)