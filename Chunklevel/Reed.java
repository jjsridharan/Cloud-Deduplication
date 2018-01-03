/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 *///
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.util.Random;

class ByteInputOutputExpCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        for (int iByte = offset; iByte < offset + byteCount; iByte++) {
            {
                final int iInput = 0;
                final byte[] inputShard = inputs[iInput];
                final byte inputByte = inputShard[iByte];
                for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                    final byte[] outputShard = outputs[iOutput];
                    final byte[] matrixRow = matrixRows[iOutput];
                    outputShard[iByte] = Galois.multiply(matrixRow[iInput], inputByte);
                }
            }

            for (int iInput = 1; iInput < inputCount; iInput++) {
                final byte[] inputShard = inputs[iInput];
                final byte inputByte = inputShard[iByte];
                for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                    final byte[] outputShard = outputs[iOutput];
                    final byte[] matrixRow = matrixRows[iOutput];
                    outputShard[iByte] ^= Galois.multiply(matrixRow[iInput], inputByte);
                }
            }
        }
    }

}




/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class ByteInputOutputTableCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        final byte[][] table = Galois.MULTIPLICATION_TABLE;

        for (int iByte = offset; iByte < offset + byteCount; iByte++) {
            {
                final int iInput = 0;
                final byte[] inputShard = inputs[iInput];
                final byte inputByte = inputShard[iByte];
                for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                    final byte[] outputShard = outputs[iOutput];
                    final byte[] matrixRow = matrixRows[iOutput];
                    final byte[] multTableRow = table[matrixRow[iInput] & 0xFF];
                    outputShard[iByte] = multTableRow[inputByte & 0xFF];
                }
            }
            for (int iInput = 1; iInput < inputCount; iInput++) {
                final byte[] inputShard = inputs[iInput];
                final byte inputByte = inputShard[iByte];
                for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                    final byte[] outputShard = outputs[iOutput];
                    final byte[] matrixRow = matrixRows[iOutput];
                    final byte[] multTableRow = table[matrixRow[iInput] & 0xFF];
                    outputShard[iByte] ^= multTableRow[inputByte & 0xFF];
                }
            }
        }
    }
}




/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class ByteOutputInputExpCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        for (int iByte = offset; iByte < offset + byteCount; iByte++) {
            for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                byte [] matrixRow = matrixRows[iOutput];
                int value = 0;
                for (int iInput = 0; iInput < inputCount; iInput++) {
                    value ^= Galois.multiply(matrixRow[iInput], inputs[iInput][iByte]);
                }
                outputs[iOutput][iByte] = (byte) value;
            }
        }
    }

}





/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class ByteOutputInputTableCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        byte [] [] table = Galois.MULTIPLICATION_TABLE;
        for (int iByte = offset; iByte < offset + byteCount; iByte++) {
            for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                byte [] matrixRow = matrixRows[iOutput];
                int value = 0;
                for (int iInput = 0; iInput < inputCount; iInput++) {
                    value ^= table[matrixRow[iInput] & 0xFF][inputs[iInput][iByte] & 0xFF];
                }
                outputs[iOutput][iByte] = (byte) value;
            }
        }
    }
}





/**
 * Interface for a method of looping over inputs and encoding them.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


interface CodingLoop {

    /**
     * All of the available coding loop algorithms.
     *
     * The different choices nest the three loops in different orders,
     * and either use the log/exponents tables, or use the multiplication
     * table.
     *
     * The naming of the three loops is (with number of loops in benchmark):
     *
     *    "byte"   - Index of byte within shard.  (200,000 bytes in each shard)
     *
     *    "input"  - Which input shard is being read.  (17 data shards)
     *
     *    "output"  - Which output shard is being computed.  (3 parity shards)
     *
     * And the naming for multiplication method is:
     *
     *    "table"  - Use the multiplication table.
     *
     *    "exp"    - Use the logarithm/exponent table.
     *
     * The ReedSolomonBenchmark class compares the performance of the different
     * loops, which will depend on the specific processor you're running on.
     *
     * This is the inner loop.  It needs to be fast.  Be careful
     * if you change it.
     *
     * I have tried inlining Galois.multiply(), but it doesn't
     * make things any faster.  The JIT compiler is known to inline
     * methods, so it's probably already doing so.
     */
    CodingLoop[] ALL_CODING_LOOPS =
            new CodingLoop[] {
                    new ByteInputOutputExpCodingLoop(),
                    new ByteInputOutputTableCodingLoop(),
                    new ByteOutputInputExpCodingLoop(),
                    new ByteOutputInputTableCodingLoop(),
                    new InputByteOutputExpCodingLoop(),
                    new InputByteOutputTableCodingLoop(),
                    new InputOutputByteExpCodingLoop(),
                    new InputOutputByteTableCodingLoop(),
                    new OutputByteInputExpCodingLoop(),
                    new OutputByteInputTableCodingLoop(),
                    new OutputInputByteExpCodingLoop(),
                    new OutputInputByteTableCodingLoop(),
            };

    /**
     * Multiplies a subset of rows from a coding matrix by a full set of
     * input shards to produce some output shards.
     *
     * @param matrixRows The rows from the matrix to use.
     * @param inputs An array of byte arrays, each of which is one input shard.
     *               The inputs array may have extra buffers after the ones
     *               that are used.  They will be ignored.  The number of
     *               inputs used is determined by the length of the
     *               each matrix row.
     * @param inputCount The number of input byte arrays.
     * @param outputs Byte arrays where the computed shards are stored.  The
     *                outputs array may also have extra, unused, elements
     *                at the end.  The number of outputs computed, and the
     *                number of matrix rows used, is determined by
     *                outputCount.
     * @param outputCount The number of outputs to compute.
     * @param offset The index in the inputs and output of the first byte
     *               to process.
     * @param byteCount The number of bytes to process.
     */
     void codeSomeShards(final byte [] [] matrixRows,
                         final byte [] [] inputs,
                         final int inputCount,
                         final byte [] [] outputs,
                         final int outputCount,
                         final int offset,
                         final int byteCount);

    /**
     * Multiplies a subset of rows from a coding matrix by a full set of
     * input shards to produce some output shards, and checks that the
     * the data is those shards matches what's expected.
     *
     * @param matrixRows The rows from the matrix to use.
     * @param inputs An array of byte arrays, each of which is one input shard.
     *               The inputs array may have extra buffers after the ones
     *               that are used.  They will be ignored.  The number of
     *               inputs used is determined by the length of the
     *               each matrix row.
     * @param inputCount THe number of input byte arrays.
     * @param toCheck Byte arrays where the computed shards are stored.  The
     *                outputs array may also have extra, unused, elements
     *                at the end.  The number of outputs computed, and the
     *                number of matrix rows used, is determined by
     *                outputCount.
     * @param checkCount The number of outputs to compute.
     * @param offset The index in the inputs and output of the first byte
     *               to process.
     * @param byteCount The number of bytes to process.
     * @param tempBuffer A place to store temporary results.  May be null.
     */
     boolean checkSomeShards(final byte [] [] matrixRows,
                             final byte [] [] inputs,
                             final int inputCount,
                             final byte [] [] toCheck,
                             final int checkCount,
                             final int offset,
                             final int byteCount,
                             final byte [] tempBuffer);
}






/**
 * Common implementations for coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


/**
 * Common implementations for coding loops.
 *
 * Many of the coding loops do not have custom checkSomeShards() methods.
 * The benchmark doesn't measure that method.
 */
abstract class CodingLoopBase implements CodingLoop {

    @Override
    public boolean checkSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] toCheck, int checkCount,
            int offset, int byteCount,
            byte[] tempBuffer) {

        // This is the loop structure for ByteOutputInput, which does not
        // require temporary buffers for checking.
        byte [] [] table = Galois.MULTIPLICATION_TABLE;
        for (int iByte = offset; iByte < offset + byteCount; iByte++) {
            for (int iOutput = 0; iOutput < checkCount; iOutput++) {
                byte [] matrixRow = matrixRows[iOutput];
                int value = 0;
                for (int iInput = 0; iInput < inputCount; iInput++) {
                    value ^= table[matrixRow[iInput] & 0xFF][inputs[iInput][iByte] & 0xFF];
                }
                if (toCheck[iOutput][iByte] != (byte) value) {
                    return false;
                }
            }
        }
        return true;
    }
}





/**
 * 8-bit Galois Field
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


/**
 * 8-bit Galois Field
 *
 * This class implements multiplication, division, addition,
 * subtraction, and exponentiation.
 *
 * The multiplication operation is in the inner loop of
 * erasure coding, so it's been optimized.  Having the
 * class be "final" helps a little, and having the EXP_TABLE
 * repeat the data, so there's no need to bound the sum
 * of two logarithms to 255 helps a lot.
 */
final class Galois {

    /**
     * The number of elements in the field.
     */

    public static final int FIELD_SIZE = 256;

    /**
     * The polynomial used to generate the logarithm table.
     *
     * There are a number of polynomials that work to generate
     * a Galois field of 256 elements.  The choice is arbitrary,
     * and we just use the first one.
     *
     * The possibilities are: 29, 43, 45, 77, 95, 99, 101, 105,
     * 113, 135, 141, 169, 195, 207, 231, and 245.
     */

    public static final int GENERATING_POLYNOMIAL = 29;

    /**
     * Mapping from members of the Galois Field to their
     * integer logarithms.  The entry for 0 is meaningless
     * because there is no log of 0.
     *
     * This array is shorts, not bytes, so that they can
     * be used directly to index arrays without casting.
     * The values (except the non-value at index 0) are
     * all really bytes, so they range from 0 to 255.
     *
     * This table was generated by java_tables.py, and the
     * unit tests check it against the Java implementation.
     */

    static final public short [] LOG_TABLE = new short [] {
            -1,    0,    1,   25,    2,   50,   26,  198,
            3,  223,   51,  238,   27,  104,  199,   75,
            4,  100,  224,   14,   52,  141,  239,  129,
            28,  193,  105,  248,  200,    8,   76,  113,
            5,  138,  101,   47,  225,   36,   15,   33,
            53,  147,  142,  218,  240,   18,  130,   69,
            29,  181,  194,  125,  106,   39,  249,  185,
            201,  154,    9,  120,   77,  228,  114,  166,
            6,  191,  139,   98,  102,  221,   48,  253,
            226,  152,   37,  179,   16,  145,   34,  136,
            54,  208,  148,  206,  143,  150,  219,  189,
            241,  210,   19,   92,  131,   56,   70,   64,
            30,   66,  182,  163,  195,   72,  126,  110,
            107,   58,   40,   84,  250,  133,  186,   61,
            202,   94,  155,  159,   10,   21,  121,   43,
            78,  212,  229,  172,  115,  243,  167,   87,
            7,  112,  192,  247,  140,  128,   99,   13,
            103,   74,  222,  237,   49,  197,  254,   24,
            227,  165,  153,  119,   38,  184,  180,  124,
            17,   68,  146,  217,   35,   32,  137,   46,
            55,   63,  209,   91,  149,  188,  207,  205,
            144,  135,  151,  178,  220,  252,  190,   97,
            242,   86,  211,  171,   20,   42,   93,  158,
            132,   60,   57,   83,   71,  109,   65,  162,
            31,   45,   67,  216,  183,  123,  164,  118,
            196,   23,   73,  236,  127,   12,  111,  246,
            108,  161,   59,   82,   41,  157,   85,  170,
            251,   96,  134,  177,  187,  204,   62,   90,
            203,   89,   95,  176,  156,  169,  160,   81,
            11,  245,   22,  235,  122,  117,   44,  215,
            79,  174,  213,  233,  230,  231,  173,  232,
            116,  214,  244,  234,  168,   80,   88,  175

    };

    /**
     * Inverse of the logarithm table.  Maps integer logarithms
     * to members of the field.  There is no entry for 255
     * because the highest log is 254.
     *
     * This table was generated by java_tables.py.
     */

    static final byte [] EXP_TABLE = new byte [] {
            1,    2,    4,    8,   16,   32,   64, -128,
            29,   58,  116,  -24,  -51, -121,   19,   38,
            76, -104,   45,   90,  -76,  117,  -22,  -55,
            -113,    3,    6,   12,   24,   48,   96,  -64,
            -99,   39,   78, -100,   37,   74, -108,   53,
            106,  -44,  -75,  119,  -18,  -63,  -97,   35,
            70, -116,    5,   10,   20,   40,   80,  -96,
            93,  -70,  105,  -46,  -71,  111,  -34,  -95,
            95,  -66,   97,  -62, -103,   47,   94,  -68,
            101,  -54, -119,   15,   30,   60,  120,  -16,
            -3,  -25,  -45,  -69,  107,  -42,  -79,  127,
            -2,  -31,  -33,  -93,   91,  -74,  113,  -30,
            -39,  -81,   67, -122,   17,   34,   68, -120,
            13,   26,   52,  104,  -48,  -67,  103,  -50,
            -127,   31,   62,  124,   -8,  -19,  -57, -109,
            59,  118,  -20,  -59, -105,   51,  102,  -52,
            -123,   23,   46,   92,  -72,  109,  -38,  -87,
            79,  -98,   33,   66, -124,   21,   42,   84,
            -88,   77, -102,   41,   82,  -92,   85,  -86,
            73, -110,   57,  114,  -28,  -43,  -73,  115,
            -26,  -47,  -65,   99,  -58, -111,   63,  126,
            -4,  -27,  -41,  -77,  123,  -10,  -15,   -1,
            -29,  -37,  -85,   75, -106,   49,   98,  -60,
            -107,   55,  110,  -36,  -91,   87,  -82,   65,
            -126,   25,   50,  100,  -56, -115,    7,   14,
            28,   56,  112,  -32,  -35,  -89,   83,  -90,
            81,  -94,   89,  -78,  121,  -14,   -7,  -17,
            -61, -101,   43,   86,  -84,   69, -118,    9,
            18,   36,   72, -112,   61,  122,  -12,  -11,
            -9,  -13,   -5,  -21,  -53, -117,   11,   22,
            44,   88,  -80,  125,   -6,  -23,  -49, -125,
            27,   54,  108,  -40,  -83,   71, -114,
            // Repeat the table a second time, so multiply()
            // does not have to check bounds.
            1,    2,    4,    8,   16,   32,   64, -128,
            29,   58,  116,  -24,  -51, -121,   19,   38,
            76, -104,   45,   90,  -76,  117,  -22,  -55,
            -113,    3,    6,   12,   24,   48,   96,  -64,
            -99,   39,   78, -100,   37,   74, -108,   53,
            106,  -44,  -75,  119,  -18,  -63,  -97,   35,
            70, -116,    5,   10,   20,   40,   80,  -96,
            93,  -70,  105,  -46,  -71,  111,  -34,  -95,
            95,  -66,   97,  -62, -103,   47,   94,  -68,
            101,  -54, -119,   15,   30,   60,  120,  -16,
            -3,  -25,  -45,  -69,  107,  -42,  -79,  127,
            -2,  -31,  -33,  -93,   91,  -74,  113,  -30,
            -39,  -81,   67, -122,   17,   34,   68, -120,
            13,   26,   52,  104,  -48,  -67,  103,  -50,
            -127,   31,   62,  124,   -8,  -19,  -57, -109,
            59,  118,  -20,  -59, -105,   51,  102,  -52,
            -123,   23,   46,   92,  -72,  109,  -38,  -87,
            79,  -98,   33,   66, -124,   21,   42,   84,
            -88,   77, -102,   41,   82,  -92,   85,  -86,
            73, -110,   57,  114,  -28,  -43,  -73,  115,
            -26,  -47,  -65,   99,  -58, -111,   63,  126,
            -4,  -27,  -41,  -77,  123,  -10,  -15,   -1,
            -29,  -37,  -85,   75, -106,   49,   98,  -60,
            -107,   55,  110,  -36,  -91,   87,  -82,   65,
            -126,   25,   50,  100,  -56, -115,    7,   14,
            28,   56,  112,  -32,  -35,  -89,   83,  -90,
            81,  -94,   89,  -78,  121,  -14,   -7,  -17,
            -61, -101,   43,   86,  -84,   69, -118,    9,
            18,   36,   72, -112,   61,  122,  -12,  -11,
            -9,  -13,   -5,  -21,  -53, -117,   11,   22,
            44,   88,  -80,  125,   -6,  -23,  -49, -125,
            27,   54,  108,  -40,  -83,   71, -114
    };

    /**
     * A multiplication table for the Galois field.
     *
     * Using this table is an alternative to using the multiply() method,
     * which uses log/exp table lookups.
     */
    public static byte [] [] MULTIPLICATION_TABLE = generateMultiplicationTable();

    /**
     * Adds two elements of the field.  If you're in an inner loop,
     * you should inline this function: it's just XOR.
     */
    public static byte add(byte a, byte b) {
        return (byte) (a ^ b);
    }

    /**
     * Inverse of addition.  If you're in an inner loop,
     * you should inline this function: it's just XOR.
     */
    public static byte subtract(byte a, byte b) {
        return (byte) (a ^ b);
    }

    /**
     * Multiplies two elements of the field.
     */
    public static byte multiply(byte a, byte b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        else {
            int logA = LOG_TABLE[a & 0xFF];
            int logB = LOG_TABLE[b & 0xFF];
            int logResult = logA + logB;
            return EXP_TABLE[logResult];
        }
    }

    /**
     * Inverse of multiplication.
     */
    public static byte divide(byte a, byte b) {
        if (a == 0) {
            return 0;
        }
        if (b == 0) {
            throw new IllegalArgumentException("Argument 'divisor' is 0");
        }
        int logA = LOG_TABLE[a & 0xFF];
        int logB = LOG_TABLE[b & 0xFF];
        int logResult = logA - logB;
        if (logResult < 0) {
            logResult += 255;
        }
        return EXP_TABLE[logResult];
    }

    /**
     * Computes a**n.
     *
     * The result will be the same as multiplying a times itself n times.
     *
     * @param a A member of the field.
     * @param n A plain-old integer.
     * @return The result of multiplying a by itself n times.
     */
    public static byte exp(byte a, int n) {
        if (n == 0) {
            return 1;
        }
        else if (a == 0) {
            return 0;
        }
        else {
            int logA = LOG_TABLE[a & 0xFF];
            int logResult = logA * n;
            while (255 <= logResult) {
                logResult -= 255;
            }
            return EXP_TABLE[logResult];
        }
    }

    /**
     * Generates a logarithm table given a starting polynomial.
     */
    public static short [] generateLogTable(int polynomial) {
        short [] result = new short[FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            result[i] = -1; // -1 means "not set"
        }
        int b = 1;
        for (int log = 0; log < FIELD_SIZE - 1; log++) {
            if (result[b] != -1) {
                throw new RuntimeException("BUG: duplicate logarithm (bad polynomial?)");
            }
            result[b] = (short) log;
            b = (b << 1);
            if (FIELD_SIZE <= b) {
                b = ((b - FIELD_SIZE) ^ polynomial);
            }
        }
        return result;
    }

    /**
     * Generates the inverse log table.
     */
    public static byte [] generateExpTable(short [] logTable) {
        final byte [] result = new byte [FIELD_SIZE * 2 - 2];
        for (int i = 1; i < FIELD_SIZE; i++) {
            int log = logTable[i];
            result[log] = (byte) i;
            result[log + FIELD_SIZE - 1] = (byte) i;
        }
        return result;
    }

    /**
     * Generates a multiplication table as an array of byte arrays.
     *
     * To get the result of multiplying a and b:
     *
     *     MULTIPLICATION_TABLE[a][b]
     */
    public static byte [] [] generateMultiplicationTable() {
        byte [] [] result = new byte [256] [256];
        for (int a = 0; a < FIELD_SIZE; a++) {
            for (int b = 0; b < FIELD_SIZE; b++) {
                result[a][b] = multiply((byte) a, (byte) b);
            }
        }
        return result;
    }

    /**
     * Returns a list of all polynomials that can be used to generate
     * the field.
     *
     * This is never used in the code; it's just here for completeness.
     */
    public static Integer [] allPossiblePolynomials() {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < FIELD_SIZE; i++) {
            try {
                generateLogTable(i);
                result.add(i);
            }
            catch (RuntimeException e) {
                // this one didn't work
            }
        }
        return result.toArray(new Integer [result.size()]);
    }

}




/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class InputByteOutputExpCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        {
            final int iInput = 0;
            final byte[] inputShard = inputs[iInput];
            for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                final byte inputByte = inputShard[iByte];
                for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                    final byte[] outputShard = outputs[iOutput];
                    final byte[] matrixRow = matrixRows[iOutput];
                    outputShard[iByte] = Galois.multiply(matrixRow[iInput], inputByte);
                }
            }
        }

        for (int iInput = 1; iInput < inputCount; iInput++) {
            final byte[] inputShard = inputs[iInput];
            for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                final byte inputByte = inputShard[iByte];
                for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                    final byte[] outputShard = outputs[iOutput];
                    final byte[] matrixRow = matrixRows[iOutput];
                    outputShard[iByte] ^= Galois.multiply(matrixRow[iInput], inputByte);
                }
            }
        }
    }

}





/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */



class InputByteOutputTableCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        final byte [] [] table = Galois.MULTIPLICATION_TABLE;

        {
            final int iInput = 0;
            final byte[] inputShard = inputs[iInput];
            for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                final byte inputByte = inputShard[iByte];
                final byte [] multTableRow = table[inputByte & 0xFF];
                for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                    final byte[] outputShard = outputs[iOutput];
                    final byte[] matrixRow = matrixRows[iOutput];
                    outputShard[iByte] = multTableRow[matrixRow[iInput] & 0xFF];
                }
            }
        }

        for (int iInput = 1; iInput < inputCount; iInput++) {
            final byte[] inputShard = inputs[iInput];
            for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                final byte inputByte = inputShard[iByte];
                final byte [] multTableRow = table[inputByte & 0xFF];
                for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                    final byte[] outputShard = outputs[iOutput];
                    final byte[] matrixRow = matrixRows[iOutput];
                    outputShard[iByte] ^= multTableRow[matrixRow[iInput] & 0xFF];
                }
            }
        }
    }

}





/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class InputOutputByteExpCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        {
            final int iInput = 0;
            final byte[] inputShard = inputs[iInput];
            for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                final byte[] outputShard = outputs[iOutput];
                final byte[] matrixRow = matrixRows[iOutput];
                final byte matrixByte = matrixRow[iInput];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] = Galois.multiply(matrixByte, inputShard[iByte]);
                }
            }
        }

        for (int iInput = 1; iInput < inputCount; iInput++) {
            final byte[] inputShard = inputs[iInput];
            for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                final byte[] outputShard = outputs[iOutput];
                final byte[] matrixRow = matrixRows[iOutput];
                final byte matrixByte = matrixRow[iInput];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] ^= Galois.multiply(matrixByte, inputShard[iByte]);
                }
            }
        }
    }
}





/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class InputOutputByteTableCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        final byte [] [] table = Galois.MULTIPLICATION_TABLE;

        {
            final int iInput = 0;
            final byte[] inputShard = inputs[iInput];
            for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                final byte[] outputShard = outputs[iOutput];
                final byte[] matrixRow = matrixRows[iOutput];
                final byte[] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] = multTableRow[inputShard[iByte] & 0xFF];
                }
            }
        }

        for (int iInput = 1; iInput < inputCount; iInput++) {
            final byte[] inputShard = inputs[iInput];
            for (int iOutput = 0; iOutput < outputCount; iOutput++) {
                final byte[] outputShard = outputs[iOutput];
                final byte[] matrixRow = matrixRows[iOutput];
                final byte[] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] ^= multTableRow[inputShard[iByte] & 0xFF];
                }
            }
        }
    }

    @Override
    public boolean checkSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] toCheck, int checkCount,
            int offset, int byteCount,
            byte[] tempBuffer) {

        if (tempBuffer == null) {
            return super.checkSomeShards(matrixRows, inputs, inputCount, toCheck, checkCount, offset, byteCount, null);
        }

        // This is actually the code from OutputInputByteTableCodingLoop.
        // Using the loops from this class would require multiple temp
        // buffers.

        final byte [] [] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < checkCount; iOutput++) {
            final byte [] outputShard = toCheck[iOutput];
            final byte[] matrixRow = matrixRows[iOutput];
            {
                final int iInput = 0;
                final byte [] inputShard = inputs[iInput];
                final byte [] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    tempBuffer[iByte] = multTableRow[inputShard[iByte] & 0xFF];
                }
            }
            for (int iInput = 1; iInput < inputCount; iInput++) {
                final byte [] inputShard = inputs[iInput];
                final byte [] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    tempBuffer[iByte] ^= multTableRow[inputShard[iByte] & 0xFF];
                }
            }
            for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                if (tempBuffer[iByte] != outputShard[iByte]) {
                    return false;
                }
            }
        }

        return true;
    }

}






/**
 * Matrix Algebra over an 8-bit Galois Field
 *
 * Copyright 2015, Backblaze, Inc.
 */


/**
 * A matrix over the 8-bit Galois field.
 *
 * This class is not performance-critical, so the implementations
 * are simple and straightforward.
 */
class Matrix {

    /**
     * The number of rows in the matrix.
     */
    private final int rows;

    /**
     * The number of columns in the matrix.
     */
    private final int columns;

    /**
     * The data in the matrix, in row major form.
     *
     * To get element (r, c): data[r][c]
     *
     * Because this this is computer science, and not math,
     * the indices for both the row and column start at 0.
     */
    private final byte [] [] data;

    /**
     * Initialize a matrix of zeros.
     *
     * @param initRows The number of rows in the matrix.
     * @param initColumns The number of columns in the matrix.
     */
    public Matrix(int initRows, int initColumns) {
        rows = initRows;
        columns = initColumns;
        data = new byte [rows] [];
        for (int r = 0; r < rows; r++) {
            data[r] = new byte [columns];
        }
    }

    /**
     * Initializes a matrix with the given row-major data.
     */
    public Matrix(byte [] [] initData) {
        rows = initData.length;
        columns = initData[0].length;
        data = new byte [rows] [];
        for (int r = 0; r < rows; r++) {
            if (initData[r].length != columns) {
                throw new IllegalArgumentException("Not all rows have the same number of columns");
            }
            data[r] = new byte[columns];
            for (int c = 0; c < columns; c++) {
                data[r][c] = initData[r][c];
            }
        }
    }

    /**
     * Returns an identity matrix of the given size.
     */
    public static Matrix identity(int size) {
        Matrix result = new Matrix(size, size);
        for (int i = 0; i < size; i++) {
            result.set(i, i, (byte) 1);
        }
        return result;
    }

    /**
     * Returns a human-readable string of the matrix contents.
     *
     * Example: [[1, 2], [3, 4]]
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('[');
        for (int r = 0; r < rows; r++) {
            if (r != 0) {
                result.append(", ");
            }
            result.append('[');
            for (int c = 0; c < columns; c++) {
                if (c != 0) {
                    result.append(", ");
                }
                result.append(data[r][c] & 0xFF);
            }
            result.append(']');
        }
        result.append(']');
        return result.toString();
    }

    /**
     * Returns a human-readable string of the matrix contents.
     *
     * Example:
     *    00 01 02
     *    03 04 05
     *    06 07 08
     *    09 0a 0b
     */
    public String toBigString() {
        StringBuilder result = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int value = get(r, c);
                if (value < 0) {
                    value += 256;
                }
                result.append(String.format("%02x ", value));
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Returns the number of columns in this matrix.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Returns the number of rows in this matrix.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the value at row r, column c.
     */
    public byte get(int r, int c) {
        if (r < 0 || rows <= r) {
            throw new IllegalArgumentException("Row index out of range: " + r);
        }
        if (c < 0 || columns <= c) {
            throw new IllegalArgumentException("Column index out of range: " + c);
        }
        return data[r][c];
    }

    /**
     * Sets the value at row r, column c.
     */
    public void set(int r, int c, byte value) {
        if (r < 0 || rows <= r) {
            throw new IllegalArgumentException("Row index out of range: " + r);
        }
        if (c < 0 || columns <= c) {
            throw new IllegalArgumentException("Column index out of range: " + c);
        }
        data[r][c] = value;
    }

    /**
     * Returns true iff this matrix is identical to the other.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Matrix)) {
            return false;
        }
        for (int r = 0; r < rows; r++) {
            if (!Arrays.equals(data[r], ((Matrix)other).data[r])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Multiplies this matrix (the one on the left) by another
     * matrix (the one on the right).
     */
    public Matrix times(Matrix right) {
        if (getColumns() != right.getRows()) {
            throw new IllegalArgumentException(
                    "Columns on left (" + getColumns() +") " +
                    "is different than rows on right (" + right.getRows() + ")");
        }
        Matrix result = new Matrix(getRows(), right.getColumns());
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < right.getColumns(); c++) {
                byte value = 0;
                for (int i = 0; i < getColumns(); i++) {
                    value ^= Galois.multiply(get(r, i), right.get(i, c));
                }
                result.set(r, c, value);
            }
        }
        return result;
    }

    /**
     * Returns the concatenation of this matrix and the matrix on the right.
     */
    public Matrix augment(Matrix right) {
        if (rows != right.rows) {
            throw new IllegalArgumentException("Matrices don't have the same number of rows");
        }
        Matrix result = new Matrix(rows, columns + right.columns);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                result.data[r][c] = data[r][c];
            }
            for (int c = 0; c < right.columns; c++) {
                result.data[r][columns + c] = right.data[r][c];
            }
        }
        return result;
    }

    /**
     * Returns a part of this matrix.
     */
    public Matrix submatrix(int rmin, int cmin, int rmax, int cmax) {
        Matrix result = new Matrix(rmax - rmin, cmax - cmin);
        for (int r = rmin; r < rmax; r++) {
            for (int c = cmin; c < cmax; c++) {
                result.data[r - rmin][c - cmin] = data[r][c];
            }
        }
        return result;
    }

    /**
     * Returns one row of the matrix as a byte array.
     */
    public byte [] getRow(int row) {
        byte [] result = new byte [columns];
        for (int c = 0; c < columns; c++) {
            result[c] = get(row, c);
        }
        return result;
    }

    /**
     * Exchanges two rows in the matrix.
     */
    public void swapRows(int r1, int r2) {
        if (r1 < 0 || rows <= r1 || r2 < 0 || rows <= r2) {
            throw new IllegalArgumentException("Row index out of range");
        }
        byte [] tmp = data[r1];
        data[r1] = data[r2];
        data[r2] = tmp;
    }

    /**
     * Returns the inverse of this matrix.
     *
     * @throws IllegalArgumentException when the matrix is singular and
     * doesn't have an inverse.
     */
    public Matrix invert() {
        // Sanity check.
        if (rows != columns) {
            throw new IllegalArgumentException("Only square matrices can be inverted");
        }

        // Create a working matrix by augmenting this one with
        // an identity matrix on the right.
        Matrix work = augment(identity(rows));

        // Do Gaussian elimination to transform the left half into
        // an identity matrix.
        work.gaussianElimination();

        // The right half is now the inverse.
        return work.submatrix(0, rows, columns, columns * 2);
    }

    /**
     * Does the work of matrix inversion.
     *
     * Assumes that this is an r by 2r matrix.
     */
    private void gaussianElimination() {
        // Clear out the part below the main diagonal and scale the main
        // diagonal to be 1.
        for (int r = 0; r < rows; r++) {
            // If the element on the diagonal is 0, find a row below
            // that has a non-zero and swap them.
            if (data[r][r] == (byte) 0) {
                for (int rowBelow = r + 1; rowBelow < rows; rowBelow++) {
                    if (data[rowBelow][r] != 0) {
                        swapRows(r, rowBelow);
                        break;
                    }
                }
            }
            // If we couldn't find one, the matrix is singular.
            if (data[r][r] == (byte) 0) {
                throw new IllegalArgumentException("Matrix is singular");
            }
            // Scale to 1.
            if (data[r][r] != (byte) 1) {
                byte scale = Galois.divide((byte) 1, data[r][r]);
                for (int c = 0; c < columns; c++) {
                    data[r][c] = Galois.multiply(data[r][c], scale);
                }
            }
            // Make everything below the 1 be a 0 by subtracting
            // a multiple of it.  (Subtraction and addition are
            // both exclusive or in the Galois field.)
            for (int rowBelow = r + 1; rowBelow < rows; rowBelow++) {
                if (data[rowBelow][r] != (byte) 0) {
                    byte scale = data[rowBelow][r];
                    for (int c = 0; c < columns; c++) {
                        data[rowBelow][c] ^= Galois.multiply(scale, data[r][c]);
                    }
                }
            }
        }

        // Now clear the part above the main diagonal.
        for (int d = 0; d < rows; d++) {
            for (int rowAbove = 0; rowAbove < d; rowAbove++) {
                if (data[rowAbove][d] != (byte) 0) {
                    byte scale = data[rowAbove][d];
                    for (int c = 0; c < columns; c++) {
                        data[rowAbove][c] ^= Galois.multiply(scale, data[d][c]);
                    }

                }
            }
        }
    }

}






/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class OutputByteInputExpCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        for (int iOutput = 0; iOutput < outputCount; iOutput++) {
            final byte[] outputShard = outputs[iOutput];
            final byte[] matrixRow = matrixRows[iOutput];
            for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                int value = 0;
                for (int iInput = 0; iInput < inputCount; iInput++) {
                    final byte[] inputShard = inputs[iInput];
                    value ^= Galois.multiply(matrixRow[iInput], inputShard[iByte]);
                }
                outputShard[iByte] = (byte) value;
            }
        }
    }

}






/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class OutputByteInputTableCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        final byte [] [] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < outputCount; iOutput++) {
            final byte[] outputShard = outputs[iOutput];
            final byte[] matrixRow = matrixRows[iOutput];
            for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                int value = 0;
                for (int iInput = 0; iInput < inputCount; iInput++) {
                    final byte[] inputShard = inputs[iInput];
                    final byte[] multTableRow = table[matrixRow[iInput] & 0xFF];
                    value ^= multTableRow[inputShard[iByte] & 0xFF];
                }
                outputShard[iByte] = (byte) value;
            }
        }
    }

}




/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class OutputInputByteExpCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        for (int iOutput = 0; iOutput < outputCount; iOutput++) {
            final byte [] outputShard = outputs[iOutput];
            final byte [] matrixRow = matrixRows[iOutput];
            {
                final int iInput = 0;
                final byte [] inputShard = inputs[iInput];
                final byte matrixByte = matrixRow[iInput];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] = Galois.multiply(matrixByte, inputShard[iByte]);
                }
            }
            for (int iInput = 1; iInput < inputCount; iInput++) {
                final byte [] inputShard = inputs[iInput];
                final byte matrixByte = matrixRow[iInput];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] ^= Galois.multiply(matrixByte, inputShard[iByte]);
                }
            }
        }
    }

}





/**
 * One specific ordering/nesting of the coding loops.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


class OutputInputByteTableCodingLoop extends CodingLoopBase {

    @Override
    public void codeSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] outputs, int outputCount,
            int offset, int byteCount) {

        final byte [] [] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < outputCount; iOutput++) {
            final byte [] outputShard = outputs[iOutput];
            final byte[] matrixRow = matrixRows[iOutput];
            {
                final int iInput = 0;
                final byte [] inputShard = inputs[iInput];
                final byte [] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] = multTableRow[inputShard[iByte] & 0xFF];
                }
            }
            for (int iInput = 1; iInput < inputCount; iInput++) {
                final byte [] inputShard = inputs[iInput];
                final byte [] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] ^= multTableRow[inputShard[iByte] & 0xFF];
                }
            }
        }
    }

    @Override
    public boolean checkSomeShards(
            byte[][] matrixRows,
            byte[][] inputs, int inputCount,
            byte[][] toCheck, int checkCount,
            int offset, int byteCount,
            byte[] tempBuffer) {

        if (tempBuffer == null) {
            return super.checkSomeShards(matrixRows, inputs, inputCount, toCheck, checkCount, offset, byteCount, null);
        }

        final byte [] [] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < checkCount; iOutput++) {
            final byte [] outputShard = toCheck[iOutput];
            final byte[] matrixRow = matrixRows[iOutput];
            {
                final int iInput = 0;
                final byte [] inputShard = inputs[iInput];
                final byte [] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    tempBuffer[iByte] = multTableRow[inputShard[iByte] & 0xFF];
                }
            }
            for (int iInput = 1; iInput < inputCount; iInput++) {
                final byte [] inputShard = inputs[iInput];
                final byte [] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    tempBuffer[iByte] ^= multTableRow[inputShard[iByte] & 0xFF];
                }
            }
            for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                if (tempBuffer[iByte] != outputShard[iByte]) {
                    return false;
                }
            }
        }

        return true;
    }
}






/**
 * Reed-Solomon Coding over 8-bit values.
 *
 * Copyright 2015, Backblaze, Inc.
 */


/**
 * Reed-Solomon Coding over 8-bit values.
 */
class ReedSolomon {

    private final int dataShardCount;
    private final int parityShardCount;
    private final int totalShardCount;
    private final Matrix matrix;
    private final CodingLoop codingLoop;

    /**
     * Rows from the matrix for encoding parity, each one as its own
     * byte array to allow for efficient access while encoding.
     */
    private final byte [] [] parityRows;

    /**
     * Creates a ReedSolomon codec with the default coding loop.
     */
    public static ReedSolomon create(int dataShardCount, int parityShardCount) {
        return new ReedSolomon(dataShardCount, parityShardCount, new InputOutputByteTableCodingLoop());
    }

    /**
     * Initializes a new encoder/decoder, with a chosen coding loop.
     */
    public ReedSolomon(int dataShardCount, int parityShardCount, CodingLoop codingLoop) {

        // We can have at most 256 shards total, as any more would
        // lead to duplicate rows in the Vandermonde matrix, which
        // would then lead to duplicate rows in the built matrix
        // below. Then any subset of the rows containing the duplicate
        // rows would be singular.
        if (256 < dataShardCount + parityShardCount) {
            throw new IllegalArgumentException("too many shards - max is 256");
        }

        this.dataShardCount = dataShardCount;
        this.parityShardCount = parityShardCount;
        this.codingLoop = codingLoop;
        this.totalShardCount = dataShardCount + parityShardCount;
        matrix = buildMatrix(dataShardCount, this.totalShardCount);
        parityRows = new byte [parityShardCount] [];
        for (int i = 0; i < parityShardCount; i++) {
            parityRows[i] = matrix.getRow(dataShardCount + i);
        }
    }

    /**
     * Returns the number of data shards.
     */
    public int getDataShardCount() {
        return dataShardCount;
    }

    /**
     * Returns the number of parity shards.
     */
    public int getParityShardCount() {
        return parityShardCount;
    }

    /**
     * Returns the total number of shards.
     */
    public int getTotalShardCount() {
        return totalShardCount;
    }

    /**
     * Encodes parity for a set of data shards.
     *
     * @param shards An array containing data shards followed by parity shards.
     *               Each shard is a byte array, and they must all be the same
     *               size.
     * @param offset The index of the first byte in each shard to encode.
     * @param byteCount The number of bytes to encode in each shard.
     *
     */
    public void encodeParity(byte[][] shards, int offset, int byteCount) {
        // Check arguments.
        checkBuffersAndSizes(shards, offset, byteCount);

        // Build the array of output buffers.
        byte [] [] outputs = new byte [parityShardCount] [];
        System.arraycopy(shards, dataShardCount, outputs, 0, parityShardCount);

        // Do the coding.
        codingLoop.codeSomeShards(
                parityRows,
                shards, dataShardCount,
                outputs, parityShardCount,
                offset, byteCount);
    }

    /**
     * Returns true if the parity shards contain the right data.
     *
     * @param shards An array containing data shards followed by parity shards.
     *               Each shard is a byte array, and they must all be the same
     *               size.
     * @param firstByte The index of the first byte in each shard to check.
     * @param byteCount The number of bytes to check in each shard.
     */
    public boolean isParityCorrect(byte[][] shards, int firstByte, int byteCount) {
        // Check arguments.
        checkBuffersAndSizes(shards, firstByte, byteCount);

        // Build the array of buffers being checked.
        byte [] [] toCheck = new byte [parityShardCount] [];
        System.arraycopy(shards, dataShardCount, toCheck, 0, parityShardCount);

        // Do the checking.
        return codingLoop.checkSomeShards(
                parityRows,
                shards, dataShardCount,
                toCheck, parityShardCount,
                firstByte, byteCount,
                null);
    }

    /**
     * Returns true if the parity shards contain the right data.
     *
     * This method may be significantly faster than the one above that does
     * not use a temporary buffer.
     *
     * @param shards An array containing data shards followed by parity shards.
     *               Each shard is a byte array, and they must all be the same
     *               size.
     * @param firstByte The index of the first byte in each shard to check.
     * @param byteCount The number of bytes to check in each shard.
     * @param tempBuffer A temporary buffer (the same size as each of the
     *                   shards) to use when computing parity.
     */
    public boolean isParityCorrect(byte[][] shards, int firstByte, int byteCount, byte [] tempBuffer) {
        // Check arguments.
        checkBuffersAndSizes(shards, firstByte, byteCount);
        if (tempBuffer.length < firstByte + byteCount) {
            throw new IllegalArgumentException("tempBuffer is not big enough");
        }

        // Build the array of buffers being checked.
        byte [] [] toCheck = new byte [parityShardCount] [];
        System.arraycopy(shards, dataShardCount, toCheck, 0, parityShardCount);

        // Do the checking.
        return codingLoop.checkSomeShards(
                parityRows,
                shards, dataShardCount,
                toCheck, parityShardCount,
                firstByte, byteCount,
                tempBuffer);
    }

    /**
     * Given a list of shards, some of which contain data, fills in the
     * ones that don't have data.
     *
     * Quickly does nothing if all of the shards are present.
     *
     * If any shards are missing (based on the flags in shardsPresent),
     * the data in those shards is recomputed and filled in.
     */
    public void decodeMissing(byte [] [] shards,
                              boolean [] shardPresent,
                              final int offset,
                              final int byteCount) {
        // Check arguments.
        checkBuffersAndSizes(shards, offset, byteCount);

        // Quick check: are all of the shards present?  If so, there's
        // nothing to do.
        int numberPresent = 0;
        for (int i = 0; i < totalShardCount; i++) {
            if (shardPresent[i]) {
                numberPresent += 1;
            }
        }
        if (numberPresent == totalShardCount) {
            // Cool.  All of the shards data data.  We don't
            // need to do anything.
            return;
        }

        // More complete sanity check
        if (numberPresent < dataShardCount) {
            throw new IllegalArgumentException("Not enough shards present");
        }

        // Pull out the rows of the matrix that correspond to the
        // shards that we have and build a square matrix.  This
        // matrix could be used to generate the shards that we have
        // from the original data.
        //
        // Also, pull out an array holding just the shards that
        // correspond to the rows of the submatrix.  These shards
        // will be the input to the decoding process that re-creates
        // the missing data shards.
        Matrix subMatrix = new Matrix(dataShardCount, dataShardCount);
        byte [] [] subShards = new byte [dataShardCount] [];
        {
            int subMatrixRow = 0;
            for (int matrixRow = 0; matrixRow < totalShardCount && subMatrixRow < dataShardCount; matrixRow++) {
                if (shardPresent[matrixRow]) {
                    for (int c = 0; c < dataShardCount; c++) {
                        subMatrix.set(subMatrixRow, c, matrix.get(matrixRow, c));
                    }
                    subShards[subMatrixRow] = shards[matrixRow];
                    subMatrixRow += 1;
                }
            }
        }

        // Invert the matrix, so we can go from the encoded shards
        // back to the original data.  Then pull out the row that
        // generates the shard that we want to decode.  Note that
        // since this matrix maps back to the orginal data, it can
        // be used to create a data shard, but not a parity shard.
        Matrix dataDecodeMatrix = subMatrix.invert();

        // Re-create any data shards that were missing.
        //
        // The input to the coding is all of the shards we actually
        // have, and the output is the missing data shards.  The computation
        // is done using the special decode matrix we just built.
        byte [] [] outputs = new byte [parityShardCount] [];
        byte [] [] matrixRows = new byte [parityShardCount] [];
        int outputCount = 0;
        for (int iShard = 0; iShard < dataShardCount; iShard++) {
            if (!shardPresent[iShard]) {
                outputs[outputCount] = shards[iShard];
                matrixRows[outputCount] = dataDecodeMatrix.getRow(iShard);
                outputCount += 1;
            }
        }
        codingLoop.codeSomeShards(
                matrixRows,
                subShards, dataShardCount,
                outputs, outputCount,
                offset, byteCount);

        // Now that we have all of the data shards intact, we can
        // compute any of the parity that is missing.
        //
        // The input to the coding is ALL of the data shards, including
        // any that we just calculated.  The output is whichever of the
        // data shards were missing.
        outputCount = 0;
        for (int iShard = dataShardCount; iShard < totalShardCount; iShard++) {
            if (!shardPresent[iShard]) {
                outputs[outputCount] = shards[iShard];
                matrixRows[outputCount] = parityRows[iShard - dataShardCount];
                outputCount += 1;
            }
        }
        codingLoop.codeSomeShards(
                matrixRows,
                shards, dataShardCount,
                outputs, outputCount,
                offset, byteCount);
    }

    /**
     * Checks the consistency of arguments passed to public methods.
     */
    private void checkBuffersAndSizes(byte [] [] shards, int offset, int byteCount) {
        // The number of buffers should be equal to the number of
        // data shards plus the number of parity shards.
        if (shards.length != totalShardCount) {
            throw new IllegalArgumentException("wrong number of shards: " + shards.length);
        }

        // All of the shard buffers should be the same length.
        int shardLength = shards[0].length;
        for (int i = 1; i < shards.length; i++) {
            if (shards[i].length != shardLength) {
                throw new IllegalArgumentException("Shards are different sizes");
            }
        }

        // The offset and byteCount must be non-negative and fit in the buffers.
        if (offset < 0) {
            throw new IllegalArgumentException("offset is negative: " + offset);
        }
        if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount is negative: " + byteCount);
        }
        if (shardLength < offset + byteCount) {
            throw new IllegalArgumentException("buffers to small: " + byteCount + offset);
        }
    }

    /**
     * Create the matrix to use for encoding, given the number of
     * data shards and the number of total shards.
     *
     * The top square of the matrix is guaranteed to be an identity
     * matrix, which means that the data shards are unchanged after
     * encoding.
     */
    private static Matrix buildMatrix(int dataShards, int totalShards) {
        // Start with a Vandermonde matrix.  This matrix would work,
        // in theory, but doesn't have the property that the data
        // shards are unchanged after encoding.
        Matrix vandermonde = vandermonde(totalShards, dataShards);

        // Multiple by the inverse of the top square of the matrix.
        // This will make the top square be the identity matrix, but
        // preserve the property that any square subset of rows is
        // invertible.
        Matrix top = vandermonde.submatrix(0, 0, dataShards, dataShards);
        return vandermonde.times(top.invert());
    }

    /**
     * Create a Vandermonde matrix, which is guaranteed to have the
     * property that any subset of rows that forms a square matrix
     * is invertible.
     *
     * @param rows Number of rows in the result.
     * @param cols Number of columns in the result.
     * @return A Matrix.
     */
    private static Matrix vandermonde(int rows, int cols) {
        Matrix result = new Matrix(rows, cols);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result.set(r, c, Galois.exp((byte) r, c));
            }
        }
        return result;
    }
}





/**
 * Benchmark of Reed-Solomon encoding.
 *
 * Copyright 2015, Backblaze, Inc.  All rights reserved.
 */


/**
 * Benchmark of Reed-Solomon encoding.
 *
 * Counts the number of bytes of input data that can be processed per
 * second.
 *
 * The set of data the test runs over is twice as big as the L3 cache
 * in a Xeon processor, so it should simulate the case where data has
 * been read in from a socket.
 */
class ReedSolomonBenchmark {

    private static final int DATA_COUNT = 17;
    private static final int PARITY_COUNT = 3;
    private static final int TOTAL_COUNT = DATA_COUNT + PARITY_COUNT;
    private static final int BUFFER_SIZE = 200 * 1000;
    private static final int PROCESSOR_CACHE_SIZE = 10 * 1024 * 1024;
    private static final int TWICE_PROCESSOR_CACHE_SIZE = 2 * PROCESSOR_CACHE_SIZE;
    private static final int NUMBER_OF_BUFFER_SETS = TWICE_PROCESSOR_CACHE_SIZE / DATA_COUNT / BUFFER_SIZE + 1;

    private static final long MEASUREMENT_DURATION = 2 * 1000;

    private static final Random random = new Random();

    private int nextBuffer = 0;

    public static void main(String [] args) {
        (new ReedSolomonBenchmark()).run();
    }

    public void run() {

        System.out.println("preparing...");
        final BufferSet [] bufferSets = new BufferSet [NUMBER_OF_BUFFER_SETS];
        for (int iBufferSet = 0; iBufferSet < NUMBER_OF_BUFFER_SETS; iBufferSet++) {
            bufferSets[iBufferSet] = new BufferSet();
        }
        final byte [] tempBuffer = new byte [BUFFER_SIZE];

        List<String> summaryLines = new ArrayList<String>();
        StringBuilder csv = new StringBuilder();
        csv.append("Outer,Middle,Inner,Multiply,Encode,Check\n");
        for (CodingLoop codingLoop : CodingLoop.ALL_CODING_LOOPS) {
            Measurement encodeAverage = new Measurement();
            {
                final String testName = codingLoop.getClass().getSimpleName() + " encodeParity";
                System.out.println("\nTEST: " + testName);
                ReedSolomon codec = new ReedSolomon(DATA_COUNT, PARITY_COUNT, codingLoop);
                System.out.println("    warm up...");
                doOneEncodeMeasurement(codec, bufferSets);
                doOneEncodeMeasurement(codec, bufferSets);
                System.out.println("    testing...");
                for (int iMeasurement = 0; iMeasurement < 10; iMeasurement++) {
                    encodeAverage.add(doOneEncodeMeasurement(codec, bufferSets));
                }
                System.out.println(String.format("\nAVERAGE: %s", encodeAverage));
                summaryLines.add(String.format("    %-45s %s", testName, encodeAverage));
            }
            // The encoding test should have filled all of the buffers with
            // correct parity, so we can benchmark parity checking.
            Measurement checkAverage = new Measurement();
            {
                final String testName = codingLoop.getClass().getSimpleName() + " isParityCorrect";
                System.out.println("\nTEST: " + testName);
                ReedSolomon codec = new ReedSolomon(DATA_COUNT, PARITY_COUNT, codingLoop);
                System.out.println("    warm up...");
                doOneEncodeMeasurement(codec, bufferSets);
                doOneEncodeMeasurement(codec, bufferSets);
                System.out.println("    testing...");
                for (int iMeasurement = 0; iMeasurement < 10; iMeasurement++) {
                    checkAverage.add(doOneCheckMeasurement(codec, bufferSets, tempBuffer));
                }
                System.out.println(String.format("\nAVERAGE: %s", checkAverage));
                summaryLines.add(String.format("    %-45s %s", testName, checkAverage));
            }
            csv.append(codingLoopNameToCsvPrefix(codingLoop.getClass().getSimpleName()));
            csv.append(encodeAverage.getRate());
            csv.append(",");
            csv.append(checkAverage.getRate());
            csv.append("\n");
        }

        System.out.println("\n");
        System.out.println(csv.toString());

        System.out.println("\nSummary:\n");
        for (String line : summaryLines) {
            System.out.println(line);
        }
    }

    private Measurement doOneEncodeMeasurement(ReedSolomon codec, BufferSet[] bufferSets) {
        long passesCompleted = 0;
        long bytesEncoded = 0;
        long encodingTime = 0;
        while (encodingTime < MEASUREMENT_DURATION) {
            BufferSet bufferSet = bufferSets[nextBuffer];
            nextBuffer = (nextBuffer + 1) % bufferSets.length;
            byte[][] shards = bufferSet.buffers;
            long startTime = System.currentTimeMillis();
            codec.encodeParity(shards, 0, BUFFER_SIZE);
            long endTime = System.currentTimeMillis();
            encodingTime += (endTime - startTime);
            bytesEncoded += BUFFER_SIZE * DATA_COUNT;
            passesCompleted += 1;
        }
        double seconds = ((double)encodingTime) / 1000.0;
        double megabytes = ((double)bytesEncoded) / 1000000.0;
        Measurement result = new Measurement(megabytes, seconds);
        System.out.println(String.format("        %s passes, %s", passesCompleted, result));
        return result;
    }

    private Measurement doOneCheckMeasurement(ReedSolomon codec, BufferSet[] bufferSets, byte [] tempBuffer) {
        long passesCompleted = 0;
        long bytesChecked = 0;
        long checkingTime = 0;
        while (checkingTime < MEASUREMENT_DURATION) {
            BufferSet bufferSet = bufferSets[nextBuffer];
            nextBuffer = (nextBuffer + 1) % bufferSets.length;
            byte[][] shards = bufferSet.buffers;
            long startTime = System.currentTimeMillis();
            if (!codec.isParityCorrect(shards, 0, BUFFER_SIZE, tempBuffer)) {
                // if the parity is not correct, it will throw off the
                // benchmarking because it may return early.
                throw new RuntimeException("parity not correct");
            }
            long endTime = System.currentTimeMillis();
            checkingTime += (endTime - startTime);
            bytesChecked += BUFFER_SIZE * DATA_COUNT;
            passesCompleted += 1;
        }
        double seconds = ((double)checkingTime) / 1000.0;
        double megabytes = ((double)bytesChecked) / 1000000.0;
        Measurement result = new Measurement(megabytes, seconds);
        System.out.println(String.format("        %s passes, %s", passesCompleted, result));
        return result;
    }

    /**
     * Converts a name like "OutputByteInputTableCodingLoop" to
     * "output,byte,input,table,".
     */
    private static String codingLoopNameToCsvPrefix(String className) {
        List<String> names = splitCamelCase(className);
        return
                names.get(0) + "," +
                names.get(1) + "," +
                names.get(2) + "," +
                names.get(3) + ",";
    }

    /**
     * Converts a name like "OutputByteInputTableCodingLoop" to a List of
     * words: { "output", "byte", "input", "table", "coding", "loop" }
     */
    private static List<String> splitCamelCase(String className) {
        String remaining = className;
        List<String> result = new ArrayList<String>();
        while (!remaining.isEmpty()) {
            boolean found = false;
            for (int i = 1; i < remaining.length(); i++) {
                if (Character.isUpperCase(remaining.charAt(i))) {
                    result.add(remaining.substring(0, i));
                    remaining = remaining.substring(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(remaining);
                remaining = "";
            }
        }
        return result;
    }


    private static class BufferSet {

        public byte [] [] buffers;

        public byte [] bigBuffer;

        public BufferSet() {
            buffers = new byte [TOTAL_COUNT] [BUFFER_SIZE];
            for (int iBuffer = 0; iBuffer < TOTAL_COUNT; iBuffer++) {
                byte [] buffer = buffers[iBuffer];
                for (int iByte = 0; iByte < BUFFER_SIZE; iByte++) {
                    buffer[iByte] = (byte) random.nextInt(256);
                }
            }

            bigBuffer = new byte [TOTAL_COUNT * BUFFER_SIZE];
            for (int i = 0; i < TOTAL_COUNT * BUFFER_SIZE; i++) {
                bigBuffer[i] = (byte) random.nextInt(256);
            }
        }
    }

    private static class Measurement {
        private double megabytes;
        private double seconds;

        public Measurement() {
            this.megabytes = 0.0;
            this.seconds = 0.0;
        }

        public Measurement(double megabytes, double seconds) {
            this.megabytes = megabytes;
            this.seconds = seconds;
        }

        public void add(Measurement other) {
            megabytes += other.megabytes;
            seconds += other.seconds;
        }

        public double getRate() {
            return megabytes / seconds;
        }

        @Override
        public String toString() {
            return String.format("%5.1f MB/s", getRate());
        }
    }
}




