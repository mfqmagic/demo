package com.mfq.process;

/**
 * 子プロセス
 */
public class SubProcess {

	public static void main(String[] args) {
		for (int i = 1; i <= 1000; i++) {
			// 標準出力
			System.out.println("これは標準出力：" + i + "番。");
			// 標準エラー
			System.err.println("これは標準エラー：" + i + "番。");
		}
	}
}
