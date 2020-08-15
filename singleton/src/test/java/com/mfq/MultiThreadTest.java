package com.mfq;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class MultiThreadTest {
    public static void main(String[] args) {
        System.out.println("hashcodeにより、2オブジェクトのみを作成する");
        System.out.println("RESULT:");
        for (int i = 0; i < 10; i++) {
            Thread threadA = new Thread(new ThreadA());
            Thread threadB = new Thread(new ThreadB());

            threadA.start();
            threadB.start();
        }
    }

    static class ThreadA implements Runnable {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                JAXBContext singleton;
                try {
                    singleton = JAXBContextManager.getContext(ADto.class);
//                    System.out.println("ThreadA:" + singleton.hashCode());
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println(endTime - startTime);
        }
    }

    static class ThreadB implements Runnable {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                JAXBContext singleton;
                try {
                    singleton = JAXBContextManager.getContext(BDto.class);
//                    System.out.println("ThreadB:" + singleton.hashCode());
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println(endTime - startTime);
        }
    }
}
