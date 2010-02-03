declare namespace fn = "sudoku";

declare variable  $board as xs:integer+ := (
1,0,0,  3,0,0,  6,0,0,
0,2,0,  5,0,0,  0,0,4,
0,0,9,  0,0,0,  5,2,0,

0,0,0,  9,6,3,  0,0,0,
7,1,6,  0,0,0,  0,0,0,
0,0,0,  0,8,0,  0,4,0,

9,0,0,  0,0,5,  3,0,7,
8,0,0,  4,0,6,  0,0,0,
3,5,0,  0,0,0,  0,0,1);

declare variable  $rowStarts as xs:integer+  := (1, 10, 19, 28, 37, 46, 55, 64,73);

declare variable  $groups as xs:integer+  := (
1,1,1,  2,2,2,  3,3,3,
1,1,1,  2,2,2,  3,3,3,
1,1,1,  2,2,2,  3,3,3,

4,4,4,  5,5,5,  6,6,6,
4,4,4,  5,5,5,  6,6,6,
4,4,4,  5,5,5,  6,6,6,

7,7,7,  8,8,8,  9,9,9,
7,7,7,  8,8,8,  9,9,9,
7,7,7,  8,8,8,  9,9,9
);


declare function local:getRow ($board as xs:integer+, $index as xs:integer) {
 let $rowStart := floor(($index - 1) div 9) * 9
 return
  $board[position() > $rowStart and position() <= $rowStart + 9]
};


declare  function local:getCol ($board as xs:integer+, $index as xs:integer)  as xs:integer+ {
 let $gap := ($index - 1) mod 9,
    $colIndexes := for $x in $rowStarts return $x + $gap
      return
  $board[position() = $colIndexes]};


declare  function local:getGroup ($board as xs:integer+, $index as xs:integer)  as xs:integer+ {
 let $group :=    $groups[$index]
 return
   $board[for $x in position()  return $groups[$x]= $group]
};


declare  function local:getAllowedValues ($board as xs:integer+, $index as xs:integer)  as xs:integer* {
 let $existingValues := (local:getRow($board,
$index))
return
 $existingValues
};


declare  function  local:tryValues($board as xs:integer+,
                               $emptyCells as xs:integer+,
                               $possibleValues as xs:integer+) as xs:integer* {
 let $index as xs:integer := $emptyCells[1],
    $newBoard1 as xs:integer+ := ($board[position() <$index],
                            $possibleValues[1], $board[position() > $index]),
    $result as xs:integer* := local:populateValues($newBoard1, $emptyCells[position() != 1])
   return
    $result
};

declare  function  local:populateValues($board as xs:integer+,
                                    $emptyCells as xs:integer*) as xs:integer*{
 if (not(empty($emptyCells)))
 then
 	let $index as xs:integer :=$emptyCells[1],
    	$possibleValues as xs:integer* := distinct-values(local:getAllowedValues($board, $index))
    return
    	if (count($possibleValues) > 1)
  		then
      		trace(local:tryValues($board, $emptyCells, $possibleValues),"local:tryValues")
  		else 
			if (count(trace($possibleValues,"$possibleValues")) = 1)
      		then
 				let $newBoard2 as xs:integer+ :=($board[position() <$index], $possibleValues[1], $board[position() > $index])
  				return
   					local:populateValues($newBoard2, $emptyCells[position() != 1])
    		else ()
 else
 	$board
};

declare  function  local:solveSudoku ($startBoard as xs:integer+) as  xs:integer+{
 let $emptyCells as xs:integer* := for $x in (1 to 81) return if ($startBoard[$x] = 0) then $x else (),
     $endBoard as xs:integer* := local:populateValues($startBoard,$emptyCells)
 return
	$endBoard
};

declare  function  local:drawResult ($board as xs:integer+) as  element(){
       <html>
               <head>
                       <title>Sudoku - XSLT</title>
                       <style>
                         table {{ border-collapse: collapse;
                                 border: 1px solid black; }}
                         td {{ padding: 10px; }}
                         .norm {{ border-left: 1px solid #CCC;
                                 border-top: 1px solid #CCC;}}
                         .csep {{ border-left: 1px solid black; }}
                         .rsep {{ border-top: 1px solid black; }}
                       </style>
               </head>
               <body>
         {local:drawBoard($board)}
               </body>
       </html>
};

declare  function  local:drawBoard ($board as xs:integer+) as  element(){
       <table>
           { for $i in 1 to 9
             return
                               <tr>
 {for $j at $p in 1 to 9
 let $pos := (($i - 1) * 9) + $j
  return
 <td class="{if ($p mod 3 = 1) then 'csep' else ('norm')}
{if ($i mod 3 = 1) then 'rsep' else ('norm')}">
       {$board[$pos]}
  </td>
}
                               </tr>
         }
       </table>
};


local:populateValues($board, 2)

(: Stylus Studio meta-information - (c) 2004-2005. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="xbird" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="custom" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;D:\workspace\xbird\lib\relaxngDatatype.jar;D:\workspace\xbird\lib\xsdlib.jar;D:\workspace\xbird\lib\optional\args4j-2.0.4.jar;D:\workspace\xbird\build&quot; xbird.client.InteractiveShell -o %3 -q %2 -encoding &quot;UTF-8&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/><scenario default="no" name="built-in" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="internal" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/><scenario default="no" name="saxon" userelativepaths="yes" externalpreview="no" useresolver="yes" url="" outputurl="" processortype="saxon" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="0" user="" password="" validateoutput="no" validator="internal" customvalidator=""/><scenario default="no" name="xquest" userelativepaths="yes" externalpreview="no" useresolver="no" url="" outputurl="" processortype="custom" tcpport="0" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="java -ea -Xmx255m -cp &quot;D:/workspace/xquest-0.2/lib/commons-logging.jar;D:/workspace/xquest-0.2/lib/log4j-1.2.11.jar;D:/workspace/xquest-0.2/lib/resolver.jar;D:/workspace/xquest-0.2/lib/xquest.jar&quot; net.axyana.xquest.clapp.Qizx -out %3 &quot;C:\Documents and Settings\yui\My Documents\temp\Untitled1.xquery&quot;" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" host="" port="1150327" user="" password="" validateoutput="no" validator="internal" customvalidator=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition><TemplateContext></TemplateContext><MapperFilter side="source"></MapperFilter></MapperMetaTag>
</metaInformation>
:)