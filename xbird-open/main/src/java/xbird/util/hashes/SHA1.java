/*
 * SHA1.java - An implementation of the SHA-1 Algorithm
 *
 * This version by Chuck McManis (cmcmanis@netcom.com) and
 * still public domain.
 *
 * Based on the C code that Steve Reid wrote his header
 * was :
 *      SHA-1 in C
 *      By Steve Reid <steve@edmweb.com>
 *      100% Public Domain
 *
 *      Test Vectors (from FIPS PUB 180-1)
 *      "abc"
 *      A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D
 *      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
 *      84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1
 *      A million repetitions of "a"
 *      34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F
 */
package xbird.util.hashes;

import java.util.Arrays;

/**
 * This is a simple port of Steve Reid's SHA-1 code into Java.
 * I've run his test vectors through the code and they all pass.
 */
public final class SHA1 {

    private final boolean NSA;

    private final int[] state;
    private final byte[] digestBits;
    private long count;

    /*
     * The following array forms the basis for the transform
     * buffer. Update puts bytes into this buffer and then
     * transform adds it into the state of the digest.
     */
    private final int[] block;
    private int blockIndex;

    /**
    * Simple constructor for SHA-1
    */
    public SHA1() {
        this(true);
    }

    /**
    * Variant constructor
    * @param nsa true for SHA-1, false for the original SHA-0
    */
    public SHA1(boolean nsa) {
        this.NSA = nsa;
        this.count = 0;
        this.state = new int[5];
        initState(state);
        this.digestBits = new byte[20];
        this.block = new int[16];
        this.blockIndex = 0;
    }

    /**
    * SHA1 initialization constants
    */
    private static void initState(final int[] state) {
        state[0] = 0x67452301;
        state[1] = 0xEFCDAB89;
        state[2] = 0x98BADCFE;
        state[3] = 0x10325476;
        state[4] = 0xC3D2E1F0;
    }

    public void reset() {
        this.count = 0;
        initState(state);
        Arrays.fill(digestBits, (byte) 0);
        this.blockIndex = 0;
    }

    /*
     * These functions are taken out of #defines in Steve's
     * code. Java doesn't have a preprocessor so the first
     * step is to just promote them to real methods.
     * Later we can optimize them out into inline code,
     * note that by making them final some compilers will
     * inline them when given the -O flag.
     */
    private static final int rol(final int value, final int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }

    private final int blk0(final int i) {
        block[i] = (rol(block[i], 24) & 0xFF00FF00) | (rol(block[i], 8) & 0x00FF00FF);
        return block[i];
    }

    private final int blk(final int i) {
        block[i & 15] = block[(i + 13) & 15] ^ block[(i + 8) & 15] ^ block[(i + 2) & 15]
                ^ block[i & 15];
        if(NSA) { // this makes it SHA-1
            block[i & 15] = rol(block[i & 15], 1);
        }
        return block[i & 15];
    }

    private final void R0(final int data[], final int v, final int w, final int x, final int y, final int z, final int i) {
        data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) + blk0(i) + 0x5A827999
                + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }

    private final void R1(final int data[], final int v, final int w, final int x, final int y, final int z, final int i) {
        data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) + blk(i) + 0x5A827999
                + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }

    private final void R2(final int data[], final int v, final int w, final int x, final int y, final int z, final int i) {
        data[z] += (data[w] ^ data[x] ^ data[y]) + blk(i) + 0x6ED9EBA1 + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }

    private final void R3(final int data[], final int v, final int w, final int x, final int y, final int z, final int i) {
        data[z] += (((data[w] | data[x]) & data[y]) | (data[w] & data[x])) + blk(i) + 0x8F1BBCDC
                + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }

    private final void R4(final int data[], final int v, final int w, final int x, final int y, final int z, final int i) {
        data[z] += (data[w] ^ data[x] ^ data[y]) + blk(i) + 0xCA62C1D6 + rol(data[v], 5);
        data[w] = rol(data[w], 30);
    }

    /**
     * Hash a single 512-bit block. This is the core of the algorithm.
     *
     * Note that working with arrays is very inefficient in Java as it
     * does a class cast check each time you store into the array.
     *
     */
    private void transform() {
        final int dd[] = new int[5];
        /* Copy context->state[] to working vars */
        dd[0] = state[0];
        dd[1] = state[1];
        dd[2] = state[2];
        dd[3] = state[3];
        dd[4] = state[4];
        /* 4 rounds of 20 operations each. Loop unrolled. */
        R0(dd, 0, 1, 2, 3, 4, 0);
        R0(dd, 4, 0, 1, 2, 3, 1);
        R0(dd, 3, 4, 0, 1, 2, 2);
        R0(dd, 2, 3, 4, 0, 1, 3);
        R0(dd, 1, 2, 3, 4, 0, 4);
        R0(dd, 0, 1, 2, 3, 4, 5);
        R0(dd, 4, 0, 1, 2, 3, 6);
        R0(dd, 3, 4, 0, 1, 2, 7);
        R0(dd, 2, 3, 4, 0, 1, 8);
        R0(dd, 1, 2, 3, 4, 0, 9);
        R0(dd, 0, 1, 2, 3, 4, 10);
        R0(dd, 4, 0, 1, 2, 3, 11);
        R0(dd, 3, 4, 0, 1, 2, 12);
        R0(dd, 2, 3, 4, 0, 1, 13);
        R0(dd, 1, 2, 3, 4, 0, 14);
        R0(dd, 0, 1, 2, 3, 4, 15);
        R1(dd, 4, 0, 1, 2, 3, 16);
        R1(dd, 3, 4, 0, 1, 2, 17);
        R1(dd, 2, 3, 4, 0, 1, 18);
        R1(dd, 1, 2, 3, 4, 0, 19);
        R2(dd, 0, 1, 2, 3, 4, 20);
        R2(dd, 4, 0, 1, 2, 3, 21);
        R2(dd, 3, 4, 0, 1, 2, 22);
        R2(dd, 2, 3, 4, 0, 1, 23);
        R2(dd, 1, 2, 3, 4, 0, 24);
        R2(dd, 0, 1, 2, 3, 4, 25);
        R2(dd, 4, 0, 1, 2, 3, 26);
        R2(dd, 3, 4, 0, 1, 2, 27);
        R2(dd, 2, 3, 4, 0, 1, 28);
        R2(dd, 1, 2, 3, 4, 0, 29);
        R2(dd, 0, 1, 2, 3, 4, 30);
        R2(dd, 4, 0, 1, 2, 3, 31);
        R2(dd, 3, 4, 0, 1, 2, 32);
        R2(dd, 2, 3, 4, 0, 1, 33);
        R2(dd, 1, 2, 3, 4, 0, 34);
        R2(dd, 0, 1, 2, 3, 4, 35);
        R2(dd, 4, 0, 1, 2, 3, 36);
        R2(dd, 3, 4, 0, 1, 2, 37);
        R2(dd, 2, 3, 4, 0, 1, 38);
        R2(dd, 1, 2, 3, 4, 0, 39);
        R3(dd, 0, 1, 2, 3, 4, 40);
        R3(dd, 4, 0, 1, 2, 3, 41);
        R3(dd, 3, 4, 0, 1, 2, 42);
        R3(dd, 2, 3, 4, 0, 1, 43);
        R3(dd, 1, 2, 3, 4, 0, 44);
        R3(dd, 0, 1, 2, 3, 4, 45);
        R3(dd, 4, 0, 1, 2, 3, 46);
        R3(dd, 3, 4, 0, 1, 2, 47);
        R3(dd, 2, 3, 4, 0, 1, 48);
        R3(dd, 1, 2, 3, 4, 0, 49);
        R3(dd, 0, 1, 2, 3, 4, 50);
        R3(dd, 4, 0, 1, 2, 3, 51);
        R3(dd, 3, 4, 0, 1, 2, 52);
        R3(dd, 2, 3, 4, 0, 1, 53);
        R3(dd, 1, 2, 3, 4, 0, 54);
        R3(dd, 0, 1, 2, 3, 4, 55);
        R3(dd, 4, 0, 1, 2, 3, 56);
        R3(dd, 3, 4, 0, 1, 2, 57);
        R3(dd, 2, 3, 4, 0, 1, 58);
        R3(dd, 1, 2, 3, 4, 0, 59);
        R4(dd, 0, 1, 2, 3, 4, 60);
        R4(dd, 4, 0, 1, 2, 3, 61);
        R4(dd, 3, 4, 0, 1, 2, 62);
        R4(dd, 2, 3, 4, 0, 1, 63);
        R4(dd, 1, 2, 3, 4, 0, 64);
        R4(dd, 0, 1, 2, 3, 4, 65);
        R4(dd, 4, 0, 1, 2, 3, 66);
        R4(dd, 3, 4, 0, 1, 2, 67);
        R4(dd, 2, 3, 4, 0, 1, 68);
        R4(dd, 1, 2, 3, 4, 0, 69);
        R4(dd, 0, 1, 2, 3, 4, 70);
        R4(dd, 4, 0, 1, 2, 3, 71);
        R4(dd, 3, 4, 0, 1, 2, 72);
        R4(dd, 2, 3, 4, 0, 1, 73);
        R4(dd, 1, 2, 3, 4, 0, 74);
        R4(dd, 0, 1, 2, 3, 4, 75);
        R4(dd, 4, 0, 1, 2, 3, 76);
        R4(dd, 3, 4, 0, 1, 2, 77);
        R4(dd, 2, 3, 4, 0, 1, 78);
        R4(dd, 1, 2, 3, 4, 0, 79);
        /* Add the working vars back into context.state[] */
        state[0] += dd[0];
        state[1] += dd[1];
        state[2] += dd[2];
        state[3] += dd[3];
        state[4] += dd[4];
    }

    /**
    * zero the count and state arrays; used to support
    * Pegwit's anomalous 2-barrel hashing
    */
    public void frob() {// Pegwit's little anomaly
        count = 0;
        state[0] = state[1] = state[2] = state[3] = state[4] = 0;
    }

    /**
     * Add one byte to the digest. When this is implemented
     * all of the abstract class methods end up calling
     * this method for types other than bytes.
     * @param b byte to add
     */
    public synchronized void update(final byte b) {
        int mask = (8 * (blockIndex & 3));

        count += 8;
        block[blockIndex >> 2] &= ~(0xff << mask);
        block[blockIndex >> 2] |= (b & 0xff) << mask;
        blockIndex++;
        if(blockIndex == 64) {
            transform();
            blockIndex = 0;
        }
    }

    public void update(final byte[] data) {
        update(data, 0, data.length);
    }

    /**
     * Add many bytes to the digest.
     * @param data byte data to add
     * @param offset start byte
     * @param length number of bytes to hash
     */
    public void update(final byte[] data, final int offset, final int length) {
        for(int i = 0; i < length; ++i) {
            update(data[offset + i]);
        }
    }

    /**
     * Return completed digest.
     * @return the byte array result
     */
    public byte[] digest() {
        finish();
        final byte[] out = new byte[20];
        System.arraycopy(digestBits, 0, out, 0, digestBits.length);
        reset();
        return out;
    }

    /**
     * retrieve the value of a hash
     * @param digest int[] into which to place 5 elements
     * @param offset index of first of the 5 elements
     */
    public void extract(final int[] digest, final int dstOffset) {
        for(int i = 0; i < 5; ++i) {
            digest[i + dstOffset] = ((digestBits[4 * i + 3] << 24) & 0xFF000000)
                    | ((digestBits[4 * i + 2] << 16) & 0x00FF0000)
                    | ((digestBits[4 * i + 1] << 8) & 0x0000FF00)
                    | ((digestBits[4 * i + 0]) & 0x000000FF);
        }
    }

    /**
     * Extract 4 bytes.
     * 
     * @param offset of digest to retrieve, less than 16.
     */
    public int extract4(final int offset) {
        if(offset > 16) {
            throw new IllegalArgumentException("Illegal offset: " + offset);
        }
        return ((digestBits[offset + 3] << 24) & 0xFF000000)
                | ((digestBits[offset + 2] << 16) & 0x00FF0000)
                | ((digestBits[offset + 1] << 8) & 0x0000FF00)
                | ((digestBits[offset + 0]) & 0x000000FF);
    }

    /**
     * Complete processing on the message digest.
     */
    public void finish() {
        final byte bits[] = new byte[8];
        int i;
        for(i = 0; i < 8; i++) {
            bits[i] = (byte) ((count >>> (((7 - i) * 8))) & 0xff);
        }

        update((byte) 128);
        while(blockIndex != 56) {
            update((byte) 0);
        }
        // This should cause a transform to happen.
        for(i = 0; i < 8; ++i) {
            update(bits[i]);
        }
        for(i = 0; i < 20; i++) {
            digestBits[i] = (byte) ((state[i >> 2] >>> ((3 - (i & 3)) * 8)) & 0xff);
        }
    }

    /**
    * test driver
    */
    public static void main(String[] args) {
        SHAselfTest();
    }

    /**
    * performs a self-test - spits out the test vector outputs on System.out
    */
    private static void SHAselfTest() {
        SHA1 s = new SHA1(true);
        // This line may be safely deleted, its to make it easy to see
        // the output of the program.
        System.out.println("SHA-1 Test PROGRAM.");
        System.out.println("This code runs the test vectors through the code.");

        /*      "abc"
                A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D */

        System.out.println("First test is 'abc'");
        String z = "abc";
        s.reset();
        s.update((byte) 'a');
        s.update((byte) 'b');
        s.update((byte) 'c');
        s.finish();
        System.out.println(s.digout());
        System.out.println("A9993E364706816ABA3E25717850C26C9CD0D89D");

        /*      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
                84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1 */

        System.out.println("Next Test is 'abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq'");
        z = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        s.reset();
        for(int i = 0; i < z.length(); ++i) {
            s.update((byte) z.charAt(i));
        }
        s.finish();
        System.out.println(s.digout());
        System.out.println("84983E441C3BD26EBAAE4AA1F95129E5E54670F1");

        /*      A million repetitions of "a"
                34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F */
        long startTime = 0 - System.currentTimeMillis();

        System.out.println("Last test is 1 million 'a' characters.");
        s.reset();
        for(int i = 0; i < 1000000; i++)
            s.update((byte) 'a');
        s.finish();
        System.out.println(s.digout());
        System.out.println("34AA973CD4C4DAA4F61EEB2BDBAD27316534016F");
        startTime += System.currentTimeMillis();
        double d = ((double) startTime) / 1000.0;
        System.out.println(" done, elapsed time = " + d);
    }

    /**
     * Print out the digest in a form that can be easily compared
     * to the test vectors.
     */
    private String digout() {
        final StringBuilder sb = new StringBuilder(40);
        for(int i = 0; i < 20; i++) {
            char c1, c2;

            c1 = (char) ((digestBits[i] >>> 4) & 0xf);
            c2 = (char) (digestBits[i] & 0xf);
            c1 = (char) ((c1 > 9) ? 'A' + (c1 - 10) : '0' + c1);
            c2 = (char) ((c2 > 9) ? 'A' + (c2 - 10) : '0' + c2);
            sb.append(c1);
            sb.append(c2);
        }
        return sb.toString();
    }

}