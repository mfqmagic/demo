package com.mfq.totp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * Google AuthenticatorのTOTPジェネレータークラス
 *
 * @see <a href="https://github.com/google/google-authenticator">Google Authenticator OpenSource</a>
 * @see <a href="https://tools.ietf.org/html/rfc6238">TOTP</a>
 */
public class AuthenticatorDemo {

    // taken from Google pam docs - we probably don't need to mess with these
    public static final int SECRET_SIZE = 10;

    // 乱数生成用シード
    public static final String SEED = "g8GjEvTbW5oVSV7avLBdwIHqGlUYNzKFI7izOF8GwLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx";

    // 乱数生成用アルゴリズム
    public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

    // 時間ウィンドウのサイズのディフォルト値
    private static int window_size = 3; // default 3 - max 17 (from google docs)

    private AuthenticatorDemo() {
    }

    /**
     * 時間ウィンドウのサイズを設定する。
     * これは、許容される30秒のウィンドウの数を表す整数値です。
     * サイズが大きくなると、クロックスキューの許容度が高くなる。
     * これで攻撃される可能性も大きくなる。
     *
     * <p>サイズ範囲1～17</p>
     *
     * @param size 時間ウィンドウ
     */
    public static void setWindowSize(int size) {
        if (size >= 1 && size <= 17)
            window_size = size;
    }

    /**
     * ランダムな秘密鍵を生成する。
     * これはサーバーによって保存され、ユーザーアカウントに関連付けられて、
     * Google認証システムによって表示されるコードを検証する必要があります。
     * ユーザーはこの秘密鍵をデバイスに登録する必要があります。
     *
     * @return 秘密鍵
     */
    public static String generateSecretKey() {
        SecureRandom sr = null;
        try {
            // 乱数生成
            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decodeBase64(SEED));
            byte[] buffer = sr.generateSeed(SECRET_SIZE);

            // 乱数をBase32に変換して秘密鍵とする。
            Base32 codec = new Base32();
            byte[] bEncodedKey = codec.encode(buffer);
            String encodedKey = new String(bEncodedKey);
            return encodedKey;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("乱数生成エラー");
        }
        return null;
    }

    /**
     * 認証コードの入力チェック
     *
     * @param secret 秘密鍵
     * @param code   認証コード
     * @return true:成功；false:失敗
     */
    public static boolean checkCode(String secret, String code) {
        // 秘密鍵
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        // UNIXのミリ秒時間を30秒の「時間ウィンドウ」に変換
        // これはTOTP仕様に準拠しています（詳細はRFC6238を参照してください）
        long timeMsec = System.currentTimeMillis();
        long timeNo = (timeMsec / 1000L) / 30L;
        // 「時間ウィンドウ」は、過去に生成された認証コードをチェックするために使用されます。
        // window_sizeを使用して、どこまで進んでいくかを調整できる。
        for (int i = -window_size; i <= window_size; ++i) {
            long hash;
            try {
                hash = verifyCode(decodedKey, timeNo + i);
            } catch (Exception e) {
                // Yes, this is bad form - but
                // the exceptions thrown would be rare and a static configuration problem
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
                //return false;
            }

            String hashStr = StringUtils.leftPad(String.valueOf(hash), 6, '0');
            if (code.equals(hashStr)) {
                return true;
            }
        }
        // The validation code is invalid.
        return false;
    }

    /**
     * TOTP検証アルゴリズム
     * <p>TOTP = HMAC-SHA-1(K, (T - T0) / X)</p>
     *
     * @param key    秘密鍵
     * @param timeNo 時間ウィンドウ番号
     * @return 認証コード
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static int verifyCode(byte[] key, long timeNo) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = timeNo;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // 最初のバイトを保持する
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }
}
