(:
  -- Qizx
 $ java -Xmx255m -jar "C:\workspace\qizxopen-0.4\qizxopen.jar" -out %3 "C:\workspace\xbird-dev\docs\examples\plus.xq"
:)

declare namespace atom = "http://purl.org/atom/ns#";

declare variable $gnews := doc("http://news.google.co.jp/news?ned=us&amp;output=atom&amp;head=t");

declare function local:plus($left, $right) {
  $left + $right
};

 local:plus(1, 1.5)(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="saxon" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="524297" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)