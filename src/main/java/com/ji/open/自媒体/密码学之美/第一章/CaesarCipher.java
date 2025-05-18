package com.ji.open.自媒体.密码学之美.第一章;

public class CaesarCipher {

    /**
     * 加密方法
     * @param text 明文
     * @param shift 偏移量（密钥）
     * @return 密文
     */
    public static String encrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                char enc = (char) ((c - 'A' + shift) % 26 + 'A');
                result.append(enc);
            } else if (Character.isLowerCase(c)) {
                char enc = (char) ((c - 'a' + shift) % 26 + 'a');
                result.append(enc);
            } else {
                result.append(c); // 保留非字母字符
            }
        }

        return result.toString();
    }

    /**
     * 解密方法
     * @param text 密文
     * @param shift 偏移量（密钥）
     * @return 明文
     */
    public static String decrypt(String text, int shift) {
        return encrypt(text, 26 - (shift % 26)); // 逆向平移
    }

    // 测试用例
    public static void main(String[] args) {
        String plaintext = "Hello, World!";
        int shift = 3;

        String ciphertext = encrypt(plaintext, shift);
        String decrypted = decrypt(ciphertext, shift);

        System.out.println("原文: " + plaintext);
        System.out.println("加密: " + ciphertext);
        System.out.println("解密: " + decrypted);
    }
}
