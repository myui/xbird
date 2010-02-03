(: Copyright 2005 Makoto YUI.  All Rights Reserved. :)

declare default element namespace "http://www.w3.org/1999/xhtml";
declare namespace eg = "http://db-www.naist.jp/~makoto-y/xquery/eg";

declare variable $spec := fn:doc("http://www.w3.org/TR/xquery-xpath-parsing/"); 

declare function eg:value-except($arg1 as xdt:anyAtomicType*, $arg2 as xdt:anyAtomicType*) as xdt:anyAtomicType*
{
  fn:distinct-values($arg1[not(. = $arg2)])
};

<html>
  <body>
    <h2>XQuery Lexical States</h2>
    <h3>
      Target version: <a href="{ $spec/html/body/div/dl/dd[preceding-sibling::dt[1]/text() = "This version:"]/a/text() } ">
                        { $spec/html/body/div/h2[a/@name = "w3c-doctype"]/text() }<br/>
                      </a>
      Generated Date: { fn:current-date() }
    </h3>
    <table border="3">
      <tr>
        <td><b>Lexical State</b></td>
        <td><b>Pattern</b></td>
        <td><b>Transition To State</b></td>
      </tr>
      {
        for $doc in $spec
        let $lex_states := $doc//div[*/a/@name = "XQuery-lexical-states"]
        let $tables := $lex_states/dl
        let $row := $tables/dd/table/tbody/tr
        let $res := (for $ptns in (for $p in $row/td[1]
                                  return $p)
                    let $s := $ptns/../../../../preceding-sibling::dt[1]/text()
                    let $state := fn:substring-before(fn:substring($s, 5), " State")
                    let $trans := (for $t in $ptns/following-sibling::td/table/tbody
                                  return fn:normalize-space(fn:string($t)))
                    let $p := (for $ptn in fn:tokenize(fn:string($ptns), ",\s+")
                              return <unit><state>{ $state }</state><pattern>{ fn:normalize-space($ptn) }</pattern><transition>{ $trans }</transition></unit>)
                    return $p)
        let $resset := (for $r at $pos in $res
                       let $ptn := $r/pattern/text(),
                           $trans := $r/transition/text()
                       (: filter redundant evaluation on for-loop :)
                       let $after_state := fn:subsequence($res, $pos)
                       let $state := fn:distinct-values($after_state[pattern/text() = $ptn and transition/text() = $trans]/state/text())
                       let $before_state := fn:subsequence($res, 1, $pos - 1)
                       where every $bs in $before_state satisfies not($bs/pattern/text() = $ptn and $bs/transition/text() = $trans)
                       return
                         <tr>
                           <td>{ $state } </td>
                           <td>{ $ptn } </td>
                           <td>{ $trans } </td>
                         </tr>)
        let $gkey := fn:distinct-values($resset/td[1]/text())
        let $rr := (for $g in $gkey
                   return $resset[td[1]/text() = $g])
        return $rr
      }
    </table>
    <br/>
    <table border="3">
      <tr>
        <td>SKIP</td>
        <td>
          {
            for $doc in $spec
            let $div2 := $doc/html/body/div[@class = "body"]/div[@class = "div1"]/div[@class = "div2"]
            let $p := $div2/div/p[contains(text()[1], "whitespace is not ignored in these states:")]
            let $ns := for $token in fn:tokenize(fn:substring-before(fn:replace(fn:substring-after(fn:string($p), "states:"), ", and", ","), "."), ",")
                       return fn:normalize-space($token)
            let $allstates := (for $t in $div2/div[@class = "div3"]/dl/dt/text()
                              return fn:normalize-space(fn:substring-before(fn:substring($t, 5), " State")))
            return eg:value-except($allstates, ($ns, "OCCURRENCEINDICATOR"))
          }
        </td>
      </tr>
    </table>
  </body>
</html>

(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="no" name="built-in" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="internal" tcpport="129" profilemode="6" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/><scenario default="yes" name="saxon" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="saxon" tcpport="14" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/><scenario default="no" name="xbird" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="custom" tcpport="14" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;C:\workspace\xbird\lib\relaxngDatatype.jar;C:\workspace\xbird\lib\xsdlib.jar;C:\workspace\xbird\lib\optional\args4j-2.0.4.jar;C:\workspace\xbird\target\classes&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition><template name="xquery_body"></template></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)