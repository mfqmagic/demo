package com.mfq.totp;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/*
 * 実際には単体テストではありませんが、使用方法を示している
 */
public class AuthDemoTest {

    /**
     * Google Chart API
     */
    private final String google = "https://www.google.com/chart?cht=qr&chs=200x200&chld=M|0&chl=";

    /**
     * QRコード
     * otpauth://totp/<userId>?secret=<secretKey>&issuer=<applicationName>
     *
     * @see <a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format">QR codes</a>
     */
    private final String format = google + "otpauth://totp/%s@%s?secret=%s&issuer=AuthDemoTest";

    /**
     * TOTP認証テスト
     *
     * @throws IOException IO例外
     */
    @Test
    public void authTest() throws IOException {
        // 秘密鍵生成
        String secretKey = AuthenticatorDemo.generateSecretKey();
        // QRコードのURL。
        String url = String.format(format, "testuser", "testhost", secretKey);
        System.out.println("QRコード：" + url);

        // QRコード画像（Google経由）。
        ImageIcon icon = new ImageIcon(getImg(url));
        String msg = "認証コードを確認するため、\r\n";
        msg = msg + "スマホアプリ（Google Authenticatorなど）で\r\n";
        msg = msg + "スキャンしてください。";
        JOptionPane.showMessageDialog(null, msg, "QRコード", JOptionPane.ERROR_MESSAGE, icon);
        String inputCode = JOptionPane.showInputDialog(null, "アプリの認証コードを入力してください。");

        // 時間ウィンドウのサイズ（30秒1回）、ディフォルト値3回
        AuthenticatorDemo.setWindowSize(4);
        boolean isSuccess = AuthenticatorDemo.checkCode(secretKey, inputCode);
        assertTrue(isSuccess);
    }

    /**
     * URLのimg画像を取得
     *
     * @param url URL
     * @return 画像
     * @throws IOException IO例外
     */
    private Image getImg(String url) throws IOException {
        // ローカル使うchromeブラウザバージョン（https://chromedriver.chromium.org/）
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        // ブラウザ非表示
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        WebDriver driver = new ChromeDriver(options);
        // URLアクセス
        driver.get(url);
        // imgタグのsrcを取得
        WebElement imageElement = driver.findElement(By.tagName("img"));
        String imagePath = imageElement.getAttribute("src");
        // ブラウザを閉じる
        driver.close();
        // 画像
        URL imageUrl = new URL(imagePath);
        return ImageIO.read(imageUrl);
    }
}