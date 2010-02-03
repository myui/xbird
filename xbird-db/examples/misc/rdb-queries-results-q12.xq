declare variable $input-context1 := fn:doc("file:///C:/Software/XQTS_1_0_2/TestSources/items.xml");
declare variable $input-context2 := fn:doc("file:///C:/Software/XQTS_1_0_2/TestSources/bids.xml");

declare function local:bid_summary()
  as element()*
{
    for $i in distinct-values($input-context2//itemno)
    let $b := $input-context2//bid_tuple[itemno = $i]
    return
        <bid_count>
            <itemno>{ $i }</itemno>
            <nbids>{ count($b) }</nbids>
        </bid_count>
};

<result>
 {
    let $bid_counts := local:bid_summary(),
        $maxbids := max($bid_counts/nbids),
        $maxitemnos := $bid_counts[nbids = $maxbids]
 	return ($bid_counts, $maxbids)
}
</result> (: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="custom" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;D:\workspace\xbird\lib\commons-logging-1.0.4.jar;D:\workspace\xbird\lib\optional\args4j-2.0.4.jar;D:\workspace\xbird\build&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)