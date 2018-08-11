package com.roachf.survey.utils.cryptology;

import lombok.extern.slf4j.Slf4j;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author roach
 */

@Slf4j
public class RSA {

    /**
     * base64字符集 0..63
     */
    private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

    private static RSA rsa = null;

    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;

    private static final String KEY_ALGORITHM = "RSA";

    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String INVALID_KEY = "非法key：";
    private static final String INVALID_KEY_SPEC = "key有误：";
    private static final String NO_SUCH_ALGORITH = "不存在的算法：";

    /**
     * 初始化base64字符集表
     */
    private static byte[] codes = new byte[256];

    static {
        for (int i = 0; i < 256; i++) {
            codes[i] = -1;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            codes[i] = (byte) (i - 'A');
        }
        for (int i = 'a'; i <= 'z'; i++) {
            codes[i] = (byte) (26 + i - 'a');
        }
        for (int i = '0'; i <= '9'; i++) {
            codes[i] = (byte) (52 + i - '0');
        }
        codes['+'] = 62;
        codes['/'] = 63;
    }


    private RSA() {
    }

    public static RSA getInstance() {
        if (rsa == null) {
            rsa = new RSA();
        }
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            rsa.publicKey = (RSAPublicKey) keyPair.getPublic();
            rsa.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            log.error(NO_SUCH_ALGORITH, e);
        }

        return rsa;
    }


    /**
     * 验证签名
     *
     * @param decryptData 要验证的密文
     * @param publicKey   公钥
     * @param sign        签名信息
     * @return 返回验证成功状态
     */
    public boolean verify(String decryptData, String publicKey, String sign) {
        try {
            // 解密由base64编码的公钥
            byte[] keyBytes = base64Decode(publicKey);
            // 构造X509EncodedKeySpec对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            // KEY_ALGORITHM 指定的加密算法
            Signature signature;
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 取公钥匙对象
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(base64Decode(decryptData));
            // 验证签名是否正常
            return signature.verify(base64Decode(sign));

        } catch (NoSuchAlgorithmException e) {
            log.error(NO_SUCH_ALGORITH, e);
        } catch (SignatureException e) {
            log.error("签名有误：", e);
        } catch (InvalidKeyException e) {
            log.error(INVALID_KEY, e);
        } catch (InvalidKeySpecException e) {
            log.error(INVALID_KEY_SPEC, e);
        }
        return false;
    }


    /**
     * 数字签名
     *
     * @param data       要签名的密文
     * @param privateKey 私钥
     * @return 返回签名信息
     */
    public String sign(String data, String privateKey) {
        try {
            // 解密由base64编码的私钥
            byte[] keyBytes = base64Decode(privateKey);
            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            // KEY_ALGORITHM 指定的加密算法
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 取私钥匙对象
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(base64Decode(data));
            return encode(signature.sign());
        } catch (NoSuchAlgorithmException e) {
            log.error(NO_SUCH_ALGORITH, e);
        } catch (SignatureException e) {
            log.error("签名有误：", e);
        } catch (InvalidKeyException e) {
            log.error(INVALID_KEY, e);
        } catch (InvalidKeySpecException e) {
            log.error(INVALID_KEY_SPEC, e);
        }
        return null;
    }

    /**
     * 私钥解密
     *
     * @param data       要解密的字符串
     * @param privateKey 私钥
     * @return 返回解密后的字符串
     */
    public String decryptByPrivateKey(String data, String privateKey) {
        try {
            // 对密钥解密
            byte[] keyBytes = base64Decode(privateKey);
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key key = keyFactory.generatePrivate(pkcs8KeySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(base64Decode(data)));

        } catch (NoSuchAlgorithmException e) {
            log.error("不存在该算法：", e);
        } catch (InvalidKeyException e) {
            log.error(INVALID_KEY, e);
        } catch (InvalidKeySpecException e) {
            log.error(INVALID_KEY_SPEC, e);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 公钥加密
     *
     * @param encryptData 要加密的数据
     * @param publicKey   公钥
     * @return 返回加密的数据
     */
    public String encryptByPublicKey(String encryptData, String publicKey) {
        try {
            // 对公钥解密
            byte[] keyBytes = base64Decode(publicKey);
            // 取得公钥
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key key = keyFactory.generatePublic(x509KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return encode(cipher.doFinal(encryptData.getBytes()));
        }  catch (NoSuchAlgorithmException e) {
            log.error(NO_SUCH_ALGORITH, e);
        } catch (InvalidKeyException e) {
            log.error(INVALID_KEY, e);
        } catch (InvalidKeySpecException e) {
            log.error(INVALID_KEY_SPEC, e);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 将 base64 加码过的 String 转换成 byte[]
     *
     * @param decodeData base64码
     * @return 返回转换后的字节数组
     */
    private byte[] base64Decode(String decodeData) {
        char[] dataArr = new char[decodeData.length()];
        decodeData.getChars(0, decodeData.length(), dataArr, 0);
        return base64Decode(dataArr);
    }

    /**
     * 将一个 char[] 解码成一个 byte[]
     *
     * @param data base64字符数组
     * @return 返回解码以后的字节数组
     */
    private byte[] base64Decode(char[] data) {
        int len = ((data.length + 3) / 4) * 3;
        if (data.length > 0 && data[data.length - 1] == '=') {
            --len;
        }
        if (data.length > 1 && data[data.length - 2] == '=') {
            --len;
        }
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (int ix = 0; ix < data.length; ix++) {
            int value = codes[data[ix] & 0xFF];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte) ((accum >> shift) & 0xff);
                }
            }
        }
        if (index != out.length) {
            throw new IndexOutOfBoundsException("miscalculated data length!");
        }
        return out;
    }

    /**
     * 将 byte[] 通过base64转码成 String
     *
     * @param bytes
     * @return
     */
    public String encode(byte[] bytes) {
        return new String(base64Encode(bytes));
    }

    /**
     * 将 byte[] 转换成base64的字符数组
     *
     * @param data 字节数组
     * @return base64字符数组
     */
    private char[] base64Encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        return out;
    }


    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
