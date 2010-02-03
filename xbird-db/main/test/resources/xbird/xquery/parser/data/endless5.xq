declare function local:endlessOnes()
{ 
  (1, local:endlessOnes()) 
};

some $x in local:endlessOnes() satisfies $x eq 1