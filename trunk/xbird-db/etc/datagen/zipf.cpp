#include <stdio.h>
#include <malloc.h>
#include <stdlib.h>
#include <math.h>
#include <iostream>
#include <fstream>
#include <string.h>
#define RANGE 2
using namespace std;

/* ./a.out 4000000 1 4000000 0.86 1 */

typedef struct {
  /* Value range is all integers numbers from 1 to N, bounds included. */
  int N;
  /* Skewedness of the distribution.  Must be zero for uniform
         distributions, or positive for increasing skewedness. */
  double a;
  /* Values computed once for all random numbers for the given `N' and `a'. */
  double c1, c2;
} zipf_t;


/* Allocate a `zipf_t' and precompute values into it. */
zipf_t *zipf_init(int N, double a)
{
  zipf_t *z;

  z = (zipf_t *)malloc(sizeof(zipf_t));
  if (z == NULL) {
        fprintf(stderr, "zipf_init: malloc failed.\n");
        exit(1);
  }
  z->N = N;

  if (fabs(a) < 0.0001)
        z->a = 0;
  else if (fabs(a - 1) < 0.0001) {
        z->a = 1;
        z->c1 = log(N + 1);
  } else {
        z->a = a;
        /* Below pow(x,y) has been rewritten to exp(y*log(x)) in order to
           allow non-integer y. */
        z->c1 = exp((1-a) * log(N+1)) - 1;
        z->c2 = 1 / (1-a);
  }

  return z;
}


/* Given the struct the `zipf_init' returns, `zipf' produces
   approximately Zipfian-distributed pseudo-random numbers in the
   range from 1 to N, bounds included. */
int zipf(zipf_t *z)
{
  int r;
  double x;

  if (z->a == 0)
        return 1 + (random() % z->N);
  do {
        x = random() / (double) RAND_MAX;
        if (z->a == 1)
          x = exp(x * z->c1);
        else
          x = exp(z->c2 * log(x * z->c1 + 1));
        r = (int) x;
  } while (r <= 1 || r > z->N); /* Hedging. */
  return r;
}


int main(int argc, char *argv[]){
        int seq, len, range, seqLen, disp;
        double prob;
        int i, j;
        char outfile[20];
        char fileSuffix[30];
        zipf_t *zp;

        cout << "SKEW: 0 == NORMAL DISTRIBUTION ; 1 == CLASSIC ZIFIAN ; >> 1 == Larger values result in greater skew \n\n";
        if (argc !=6){
                cout << "INVALID NUMBER OF PARAMETERS!\n";
                cout << "USAGE: zipf numberSeq avgSeqLen numDifferentElements skew dispersion\n";
                return 0;
        }

        seq = atoi(argv[1]);  // number of sequences
        len = atoi(argv[2]);    // average length of each sequence
        range = atoi(argv[3]);  // number of different items
        prob = atof(argv[4]);   // zipf probability (0 ==> normal distribution)
        disp = atoi(argv[5]); // dispersion or standard deviation		
        
        sprintf(outfile, "zipf");
        sprintf(fileSuffix, "-S%d-L%d-R%d-P%.2f.dat", seq, len, range, prob);
        strcat(outfile, fileSuffix);
        cout << outfile << endl;
        ofstream outputfile (outfile, ios::out | ios::app );

        zp = zipf_init(range, prob);

        for (i = 0; i < seq ; i++){
                seqLen = len + (random() % disp); // get another to calculate this
                for (j= 0; j < seqLen ; j++ ){
                        outputfile << zipf(zp) << " ";
                }
                outputfile << "\n";
        }
        outputfile.close();
}
