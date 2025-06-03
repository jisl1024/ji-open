package com.ji.open.自媒体.密码学之美.第三章;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 一个简单实现的 RSA 加解密类
 * 包括：密钥生成、加密、解密，使用 BigInteger 进行大数运算
 */
public class MyRSA {

    private BigInteger privateKey[]; // 私钥数组：privateKey[0] = d, privateKey[1] = n
    private BigInteger publicKey[];  // 公钥数组：publicKey[0] = e, publicKey[1] = n

    public MyRSA() {
        privateKey = new BigInteger[2];
        publicKey = new BigInteger[2];
    }

    /**
     * 生成 RSA 密钥对：包含 e, d, n
     */
    public void genKey() {
        BigInteger p = generatePrime(); // 生成素数 p
        BigInteger q = generatePrime(); // 生成素数 q

        BigInteger n = p.multiply(q);   // n = p * q
        BigInteger phi_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // φ(n) = (p-1)*(q-1)

        BigInteger e = BigInteger.valueOf(65537); // 公钥指数，常用值 65537

        // 验证 e 与 φ(n) 是否互素
        if (!e.gcd(phi_n).equals(BigInteger.ONE)) {
            throw new RuntimeException("e 和 φ(n) 不互素");
        }

        // 计算私钥指数 d，使得 d ≡ e⁻¹ mod φ(n)
        BigInteger d = e.modInverse(phi_n);

        // 存储公钥和私钥
        publicKey[0] = e;
        publicKey[1] = n;
        privateKey[0] = d;
        privateKey[1] = n;

        // 输出相关信息
        System.out.println("p=" + p + ", q=" + q + ", n=" + n + ", φ(n)=" + phi_n);
        System.out.println("publicKey=[" + e + ", " + n + "]");
        System.out.println("privateKey=[" + d + ", " + n + "]");
    }

    /**
     * 加密明文字符串
     */
    public BigInteger encrypt(String content) {
        byte[] contentBytes = content.getBytes(); // 将明文转换为字节
        BigInteger M = new BigInteger(contentBytes); // 转换为 BigInteger

        BigInteger e = publicKey[0];
        BigInteger n = publicKey[1];

        // 检查明文是否大于模数 n
        if (M.compareTo(n) >= 0) {
            throw new RuntimeException("明文过大，不能加密。最大为 " + n + "，当前为 " + M);
        }

        // 加密公式：C = M^e mod n
        BigInteger C = M.modPow(e, n);

        System.out.println("Encrypt plaintext: " + content + ", M: " + M + ", ciphertext: " + C);
        return C;
    }

    /**
     * 解密密文 BigInteger，返回原始明文
     */
    public String decrypt(BigInteger C) {
        BigInteger d = privateKey[0];
        BigInteger n = privateKey[1];

        // 解密公式：M = C^d mod n
        BigInteger M = C.modPow(d, n);
        String content = new String(M.toByteArray()); // 转回字符串

        System.out.println("Decrypt ciphertext: " + C + ", M: " + M + ", plaintext: " + content);
        return content;
    }

    /**
     * 生成一个 64 位的大素数
     */
    private BigInteger generatePrime() {
        SecureRandom random = new SecureRandom();

        int maxAttempts = 1000;  // 最多尝试次数，避免死循环
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            BigInteger x = new BigInteger(64, random); // 随机生成 64 位大整数
            if (x.isProbablePrime(50)) { // 使用 Miller-Rabin 判断是否为素数
                return x;
            }
        }

        throw new RuntimeException("生成素数失败");
    }

    /**
     * 主函数：演示 RSA 加解密过程
     */
    public static void main(String[] args) {
        MyRSA rsa = new MyRSA();
        rsa.genKey(); // 生成密钥对
        BigInteger C = rsa.encrypt("hello world"); // 加密字符串
        System.out.println("==========================");
        rsa.decrypt(C); // 解密密文
    }
}
