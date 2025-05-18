package com.ji.open.自媒体.密码学之美.第二章;

import java.math.BigInteger;
import java.security.SecureRandom;

public class MyRSA {

    private BigInteger privateKey[];
    private BigInteger publicKey[];

    public MyRSA() {
        privateKey = new BigInteger[2];
        publicKey = new BigInteger[2];
    }

    public void genKey() {
        BigInteger p = generatePrime();
        BigInteger q = generatePrime();
        BigInteger n = p.multiply(q);
        BigInteger phi_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.valueOf(65537);

        // Check if e and phi_n are coprime
        if (!e.gcd(phi_n).equals(BigInteger.ONE)) {
            throw new RuntimeException("e and φ(n) are not coprime");
        }

        // Calculate private key exponent d
        BigInteger d = e.modInverse(phi_n);

        // Save public key and private key
        publicKey[0] = e;
        publicKey[1] = n;
        privateKey[0] = d;
        privateKey[1] = n;

        System.out.println("p=" + p + ", q=" + q + ", n=" + n + ", φ(n)=" + phi_n);
        System.out.println("publicKey=[" + e + ", " + n + "]");
        System.out.println("privateKey=[" + d + ", " + n + "]");
    }

    public BigInteger encrypt(String content) {
        byte[] contentBytes = content.getBytes();
        BigInteger M = new BigInteger(contentBytes);

        BigInteger e = publicKey[0];
        BigInteger n = publicKey[1];

        // Check if plaintext M is greater than n
        if (M.compareTo(n) >= 0) {
            throw new RuntimeException("Maximum encryption length " + n + ", current length " + M);
        }

        // Calculate ciphertext C = M^e mod n
        BigInteger C = M.modPow(e, n);

        System.out.println("Encrypt plaintext: " + content + ", M: " + M + ", ciphertext: " + C);
        return C;
    }

    public String decrypt(BigInteger C) {
        BigInteger d = privateKey[0];
        BigInteger n = privateKey[1];

        // Calculate plaintext M = C^d mod n
        BigInteger M = C.modPow(d, n);
        String content = new String(M.toByteArray());

        System.out.println("Decrypt ciphertext: " + C + ", M: " + M + ", plaintext: " + content);
        return content;
    }

    private BigInteger generatePrime() {
        SecureRandom random = new SecureRandom();

        int maxAttempts = 1000;  // Set maximum attempts to prevent infinite loop
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            BigInteger x = new BigInteger(64, random);
            if (x.isProbablePrime(50)) {
                return x;
            }
        }

        throw new RuntimeException("Failed to generate prime number");
    }

    public static void main(String[] args) {
        MyRSA rsa = new MyRSA();
        rsa.genKey();
        BigInteger C = rsa.encrypt("hello world");
        System.out.println("==========================");
        rsa.decrypt(C);
    }
}
