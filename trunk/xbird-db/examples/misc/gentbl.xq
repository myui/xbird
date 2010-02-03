<html>
<body>
<table>{
  for $y in 1 to 10 return (
    <tr>
    {
      for $x in 1 to 10
      return
        let $bg := ( if ($x mod 2 + $y mod 2 <= 0) then "lightgreen"
                  	 else if ($y mod 2 <= 0) then "yellow"
                  	 else if ($x mod 2 <= 0) then "lightblue"
                  	 else "white" ),
            $prod := $x * $y
        return 
        	<td align="right" bgcolor="{$bg}">
        	{ if ($x > 1 and $y > 1) then $prod else <b>{$prod}</b> }
         	</td>
    }
    </tr>
  )
}
</table>
</body>
</html>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="xbird" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="custom" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;D:\workspace\xbird\lib\relaxngDatatype.jar;D:\workspace\xbird\lib\xsdlib.jar;D:\workspace\xbird\lib\optional\args4j-2.0.4.jar;D:\workspace\xbird\target\classes&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="83886080" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)