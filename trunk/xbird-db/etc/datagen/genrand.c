#include <stdio.h>
#include <stdlib.h>

int main ()
{
  FILE   *fp;                   // File pointer to output file
  char   file_name[256];        // Output file name string
  int i, k, limit;
  char tmp[128];

  printf("Output file name ===================================> ");
  scanf("%s", file_name);
  fp = fopen(file_name, "w");
  if (fp == NULL) {
    printf("ERROR in creating output file (%s) \n", file_name);
    exit(1);
  }

  printf("input number of values to generate > ");
  scanf("%s", tmp);
  limit = atoi(tmp);

  srand(313410651);
  for (i=0; i <limit; i++)
  {
      k = rand() % limit;
      fprintf (fp, "%d\n", k);
  }

  printf("Done! \n");
  fclose(fp);

  return 1;
}
