package com.mfq.process;

import java.io.IOException;

/**
 * メインプロセス
 */
public class MainProcess {

	public static void main(String[] args) throws IOException, InterruptedException {
		//Process p = Runtime.getRuntime().exec("cmd /c dir");
		Process p = Runtime.getRuntime().exec("cmd /c cd process/target/classes && java com.mfq.process.SubProcess");

		// 出力ストリーム
		new StreamThread(p.getInputStream(), "OUTPUT").start();
		// エラーストリーム
		new StreamThread(p.getErrorStream(), "ERROR").start();

		// プロセス実行結果を取得
		int result = p.waitFor();
		p.destroy();

		// データを出力
		System.out.println("■実行結果コード：" + result);
	}
}