package com.ji.open.自媒体.密码学之美.第一章;

import java.util.HashMap;
import java.util.Map;

public class SubstitutionCipherLowercase {

    // 自定义的26个小写替换字母，必须不重复
    private static final String substitutionKey = "qwertyuiopasdfghjklzxcvbnm";

    // 生成加密映射表（明文小写字母 → 替换字母）
    private static Map<Character, Character> createEncryptMap() {
        Map<Character, Character> map = new HashMap<>();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 26; i++) {
            map.put(alphabet.charAt(i), substitutionKey.charAt(i));
        }
        return map;
    }

    // 生成解密映射表（密文字母 → 原始字母）
    private static Map<Character, Character> createDecryptMap() {
        Map<Character, Character> map = new HashMap<>();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 26; i++) {
            map.put(substitutionKey.charAt(i), alphabet.charAt(i));
        }
        return map;
    }

    // 加密函数
    public static String encrypt(String text) {
        Map<Character, Character> map = createEncryptMap();
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLowerCase(c)) {
                result.append(map.getOrDefault(c, c));
            } else {
                result.append(c); // 保留非小写字母
            }
        }
        return result.toString();
    }

    // 解密函数
    public static String decrypt(String text) {
        Map<Character, Character> map = createDecryptMap();
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLowerCase(c)) {
                result.append(map.getOrDefault(c, c));
            } else {
                result.append(c); // 保留非小写字母
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String plaintext = "hello, world!";
        String ciphertext = encrypt(plaintext);
        String decrypted = decrypt(ciphertext);

        System.out.println("原文: " + plaintext);
        System.out.println("加密: " + ciphertext);
        System.out.println("解密: " + decrypted);
    }
}
