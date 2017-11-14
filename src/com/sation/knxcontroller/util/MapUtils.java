package com.sation.knxcontroller.util;

import java.util.Map;

/**
 * Created by wangchunfeng on 2017/5/3.
 */

public class MapUtils {
    /**
     * 获取map中第一个数据值
     *
     * @param <K> Key的类型
     * @param <V> Value的类型
     * @param map 数据源
     * @return 返回的值
     */
    public static <K, V> V getFirstOrNull(Map<K, V> map) {
        V obj = null;

        if(null != map) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                obj = entry.getValue();
                if (obj != null) {
                    break;
                }
            }
        }

        return obj;
    }
}
