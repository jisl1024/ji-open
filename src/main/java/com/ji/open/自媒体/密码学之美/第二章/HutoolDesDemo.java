package com.ji.open.自媒体.密码学之美.第二章;

import cn.hutool.crypto.symmetric.DES;
import cn.hutool.core.util.HexUtil;

public class HutoolDesDemo {
    public static void main(String[] args) {
        // 1. 设置一个8字节密钥
        byte[] key = "12345678".getBytes(); // DES 密钥必须是8字节

        // 2. 创建 DES 实例
        DES des = new DES(key);

        // 3. 加密
        String content = "HelloDES";
        byte[] encryptBytes = des.encrypt(content); // 加密为字节数组
        String encryptHex = HexUtil.encodeHexStr(encryptBytes); // 转十六进制字符串输出
        System.out.println("加密后：" + encryptHex);

        // 4. 解密
        byte[] decryptBytes = des.decrypt(HexUtil.decodeHex(encryptHex));
        String decrypted = new String(decryptBytes);
        System.out.println("解密后：" + decrypted);
    }
}
