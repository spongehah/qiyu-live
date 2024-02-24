package org.qiyu.live.common.interfaces.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 将List拆分为小的List，用于Redis中list的存入，避免 Redis的输入输出缓冲区 和 网络 发生堵塞
 */
public class ListUtils {

    /**
     * 将一个大List集合拆分为多个子list集合（每个子集合有subNum个元素）
     */
    public static <T> List<List<T>> splistList(List<T> list, int subNum) {
        List<List<T>> resultList = new ArrayList<>();
        int priIndex = 0;
        int lastIndex = 0;
        int insertTime = list.size() / subNum;
        List<T> subList;
        for (int i = 0; i <= insertTime; i++) {
            priIndex = subNum * i;
            lastIndex = priIndex + subNum;
            if (i != insertTime) {
                subList = list.subList(priIndex, lastIndex);
            } else {
                subList = list.subList(priIndex, list.size());
            }
            if (subList.size() > 0) {
                resultList.add(subList);
            }
        }
        return resultList;
    }
}
