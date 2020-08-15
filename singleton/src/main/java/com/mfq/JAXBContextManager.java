package com.mfq;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * JAXBContext管理用クラス
 */
public final class JAXBContextManager {

    /**
     * シングルトンマップ
     */
    private static final Map<Class<?>, JAXBContext> singletonMap = new HashMap<Class<?>, JAXBContext>();

    /**
     * インスタンス化不可
     */
    private JAXBContextManager() {
    }

    /**
     * シングルトンマップ中のJAXBContext
     *
     * @param clazz クラスの型
     * @return JAXBContextオブジェクト
     * @throws JAXBException インスタントの例外
     */
    public static synchronized JAXBContext getContext(Class<?> clazz) throws JAXBException {
        if (!singletonMap.containsKey(clazz)) {
            // シングルトンマップに追加
            JAXBContext context = JAXBContext.newInstance(clazz);
            singletonMap.put(clazz, context);
            return context;
        } else {
            return singletonMap.get(clazz);
        }
    }
}
