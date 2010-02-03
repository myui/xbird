declare variable $input-context1 := fn:doc("file:///C:/Software/XQTS_1_0_2/TestSources/items.xml");
declare variable $input-context2 := fn:doc("file:///C:/Software/XQTS_1_0_2/TestSources/bids.xml");

let $highbid := max($input-context2//bid_tuple/bid)
return
  <result>
    {
      for $item in $input-context1//item_tuple,
          $b in $input-context2//bid_tuple[itemno = $item/itemno]
      where $b/bid = $highbid
      return
        <expensive_item>
          { $item/itemno }
          { $item/description }
          <high_bid>{ $highbid }</high_bid>
        </expensive_item>
    }
  </result>(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="internal" tcpport="2228264" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)