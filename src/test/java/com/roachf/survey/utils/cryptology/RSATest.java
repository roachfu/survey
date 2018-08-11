package com.roachf.survey.utils.cryptology;

import org.junit.Test;

public class RSATest {

    @Test
    public void test() {
        RSA rsa = RSA.getInstance();

        /* 获取公钥私钥 */
        String publicKey = rsa.encode(rsa.getPublicKey().getEncoded());
        String privateKey = rsa.encode(rsa.getPrivateKey().getEncoded());
        System.out.println("privateKey==" + privateKey);
        System.out.println("publicKey==" + publicKey);

        /* 公钥加密 */
        String data = rsa.encryptByPublicKey("123", publicKey);
        System.out.println(data);
        /* 私钥解密 */
        String result = rsa.decryptByPrivateKey(data, privateKey);
        System.out.println(result);

        // 签名, 验签
        String sign = rsa.sign(data, privateKey);
        boolean flag = rsa.verify(data, publicKey, sign);
        System.out.println(flag);
    }

}