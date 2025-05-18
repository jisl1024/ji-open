package com.ji.open.自媒体.密码学之美.第二章;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 纯手写 DES（Data Encryption Standard）实现
 * - 支持 64bit 数据块 / 56bit 有效密钥
 * - ECB + PKCS5Padding 作为示范
 * - 仅用于教学或兼容老系统，切勿用于生产安全
 */
public class DES {

    /* ---------------- 常量表 ---------------- */
    // 初始置换 IP
    private static final int[] IP = {
        58,50,42,34,26,18,10,2, 60,52,44,36,28,20,12,4,
        62,54,46,38,30,22,14,6, 64,56,48,40,32,24,16,8,
        57,49,41,33,25,17, 9,1, 59,51,43,35,27,19,11,3,
        61,53,45,37,29,21,13,5, 63,55,47,39,31,23,15,7
    };
    // 逆初始置换 IP‑1
    private static final int[] FP = {
        40,8,48,16,56,24,64,32, 39,7,47,15,55,23,63,31,
        38,6,46,14,54,22,62,30, 37,5,45,13,53,21,61,29,
        36,4,44,12,52,20,60,28, 35,3,43,11,51,19,59,27,
        34,2,42,10,50,18,58,26, 33,1,41, 9,49,17,57,25
    };
    // 密钥置换 PC‑1（去掉奇偶校验位，64→56）
    private static final int[] PC1 = {
        57,49,41,33,25,17, 9, 1,58,50,42,34,26,18,
        10, 2,59,51,43,35,27,19,11, 3,60,52,44,36,
        63,55,47,39,31,23,15, 7,62,54,46,38,30,22,
        14, 6,61,53,45,37,29,21,13, 5,28,20,12, 4
    };
    // 密钥置换 PC‑2（压缩置换，56→48）
    private static final int[] PC2 = {
        14,17,11,24, 1, 5, 3,28,15, 6,21,10,
        23,19,12, 4,26, 8,16, 7,27,20,13, 2,
        41,52,31,37,47,55,30,40,51,45,33,48,
        44,49,39,56,34,53,46,42,50,36,29,32
    };
    // 每轮左移位数
    private static final int[] SHIFTS = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
    // 扩展置换 E（32→48）
    private static final int[] E = {
        32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
         8, 9,10,11,12,13,12,13,14,15,16,17,
        16,17,18,19,20,21,20,21,22,23,24,25,
        24,25,26,27,28,29,28,29,30,31,32, 1
    };
    // P‑盒置换
    private static final int[] P = {
        16, 7,20,21,29,12,28,17,
         1,15,23,26, 5,18,31,10,
         2, 8,24,14,32,27, 3, 9,
        19,13,30, 6,22,11, 4,25
    };
    // 8 个 S‑盒
    private static final int[][][] SBOX = {
        { {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
          {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
          {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
          {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13} },

        { {15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
          {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
          {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
          {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9} },

        { {10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
          {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
          {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
          {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12} },

        { {7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
          {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
          {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
          {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14} },

        { {2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
          {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
          {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
          {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3} },

        { {12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
          {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
          {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
          {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13} },

        { {4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
          {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
          {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
          {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12} },

        { {13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
          {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
          {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
          {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11} }
    };

    /* ---------------- 公共接口 ---------------- */

    /** 加密字节数组（ECB，自动 PKCS5Padding） */
    public static byte[] encrypt(byte[] data, byte[] key64) {
        byte[] padded = pad(data);
        byte[][] blocks = splitBlocks(padded);
        byte[][] roundKeys = generateRoundKeys(key64);
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = encryptBlock(blocks[i], roundKeys);
        }
        return joinBlocks(blocks);
    }

    /** 解密字节数组（ECB，自动去填充） */
    public static byte[] decrypt(byte[] cipher, byte[] key64) {
        byte[][] blocks = splitBlocks(cipher);
        byte[][] roundKeys = generateRoundKeys(key64);
        // 逆序子密钥
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = decryptBlock(blocks[i], roundKeys);
        }
        byte[] joined = joinBlocks(blocks);
        return unpad(joined);
    }

    /* ---------------- 下面是核心实现 ---------------- */

    /** 生成 16 个 48bit 子密钥 */
    private static byte[][] generateRoundKeys(byte[] key64) {
        boolean[] keyBits = toBits(key64, 64);
        boolean[] permuted = permute(keyBits, PC1);              // 56
        boolean[] C = Arrays.copyOfRange(permuted, 0, 28);
        boolean[] D = Arrays.copyOfRange(permuted, 28, 56);

        byte[][] subKeys = new byte[16][6]; // 48 bits = 6 bytes
        for (int round = 0; round < 16; round++) {
            C = leftShift(C, SHIFTS[round]);
            D = leftShift(D, SHIFTS[round]);
            boolean[] CD = concat(C, D);                         // 56
            boolean[] subKeyBits = permute(CD, PC2);             // 48
            subKeys[round] = toBytes(subKeyBits, 48);
        }
        return subKeys;
    }

    public static boolean[] concat(boolean[] a, boolean[] b) {
        boolean[] result = new boolean[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }


    /** 单块加密（64bit） */
    private static byte[] encryptBlock(byte[] block, byte[][] roundKeys) {
        boolean[] bits = permute(toBits(block, 64), IP);         // 初始置换

        boolean[] L = Arrays.copyOfRange(bits, 0, 32);
        boolean[] R = Arrays.copyOfRange(bits, 32, 64);

        for (int round = 0; round < 16; round++) {
            boolean[] Rexp = permute(R, E);                      // 32→48
            boolean[] xored = xorBits(Rexp, roundKeys[round]);   // 与子密钥异或
            boolean[] sOut = sBoxSubstitution(xored);            // 48→32
            boolean[] fOut = permute(sOut, P);                   // P‑盒
            boolean[] newR = xorBits(L, fOut);
            L = R;
            R = newR;
        }
        boolean[] preoutput = concat(R, L); // 注意最后交换
        boolean[] cipherBits = permute(preoutput, FP);           // 逆初始置换
        return toBytes(cipherBits, 64);
    }

    /** 单块解密（64bit） */
    private static byte[] decryptBlock(byte[] block, byte[][] roundKeys) {
        // 加密的逆过程：子密钥逆序
        byte[][] reversed = new byte[16][];
        for (int i = 0; i < 16; i++) {
            reversed[i] = roundKeys[15 - i];
        }
        return encryptBlock(block, reversed);
    }

    /* ---------------- 工具函数 ---------------- */

    private static boolean[] permute(boolean[] input, int[] table) {
        boolean[] out = new boolean[table.length];
        for (int i = 0; i < table.length; i++) {
            out[i] = input[table[i] - 1];
        }
        return out;
    }

    private static boolean[] leftShift(boolean[] arr, int n) {
        boolean[] res = new boolean[arr.length];
        System.arraycopy(arr, n, res, 0, arr.length - n);
        System.arraycopy(arr, 0, res, arr.length - n, n);
        return res;
    }

    private static boolean[] xorBits(boolean[] a, byte[] bBytes) {
        boolean[] b = toBits(bBytes, a.length);
        boolean[] out = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] ^ b[i];
        }
        return out;
    }

    private static boolean[] xorBits(boolean[] a, boolean[] b) {
        boolean[] out = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] ^ b[i];
        }
        return out;
    }

    /** S‑盒代换（48→32 bits） */
    private static boolean[] sBoxSubstitution(boolean[] in48) {
        boolean[] out32 = new boolean[32];
        for (int i = 0; i < 8; i++) {
            int offset = i * 6;
            int row = (in48[offset] ? 2 : 0) | (in48[offset + 5] ? 1 : 0);
            int col = 0;
            for (int j = 1; j <= 4; j++) {
                col = (col << 1) | (in48[offset + j] ? 1 : 0);
            }
            int val = SBOX[i][row][col];
            for (int j = 0; j < 4; j++) {
                out32[i * 4 + (3 - j)] = (val & (1 << j)) != 0;
            }
        }
        return out32;
    }

    /* ---- 字节/位转换与分块 ---- */

    private static boolean[] toBits(byte[] bytes, int size) {
        boolean[] bits = new boolean[size];
        for (int i = 0; i < size; i++) {
            bits[i] = (bytes[i / 8] & (1 << (7 - (i % 8)))) != 0;
        }
        return bits;
    }

    private static byte[] toBytes(boolean[] bits, int size) {
        byte[] bytes = new byte[size / 8];
        for (int i = 0; i < size; i++) {
            if (bits[i]) {
                bytes[i / 8] |= 1 << (7 - (i % 8));
            }
        }
        return bytes;
    }

    private static byte[][] splitBlocks(byte[] data) {
        int n = data.length / 8;
        byte[][] blocks = new byte[n][8];
        for (int i = 0; i < n; i++) {
            System.arraycopy(data, i * 8, blocks[i], 0, 8);
        }
        return blocks;
    }

    private static byte[] joinBlocks(byte[][] blocks) {
        byte[] out = new byte[blocks.length * 8];
        for (int i = 0; i < blocks.length; i++) {
            System.arraycopy(blocks[i], 0, out, i * 8, 8);
        }
        return out;
    }

    /* ---- PKCS5 Padding ---- */

    private static byte[] pad(byte[] data) {
        int padLen = 8 - (data.length % 8);
        byte[] padded = Arrays.copyOf(data, data.length + padLen);
        Arrays.fill(padded, data.length, padded.length, (byte) padLen);
        return padded;
    }

    private static byte[] unpad(byte[] data) {
        int padLen = data[data.length - 1] & 0xFF;
        return Arrays.copyOf(data, data.length - padLen);
    }

    /* ----------------- 演示 ----------------- */
    public static void main(String[] args) throws Exception {
        String plain = "HelloDES";
        byte[] key64 = "12345678".getBytes(StandardCharsets.US_ASCII); // 8 字节 = 64bit

        byte[] cipher = DES.encrypt(plain.getBytes(StandardCharsets.UTF_8), key64);
        byte[] back  = DES.decrypt(cipher, key64);

        System.out.println("明文  : " + plain);
        System.out.println("密文  : " + bytesToHex(cipher));
        System.out.println("解密后: " + new String(back, StandardCharsets.UTF_8));



    }

    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
