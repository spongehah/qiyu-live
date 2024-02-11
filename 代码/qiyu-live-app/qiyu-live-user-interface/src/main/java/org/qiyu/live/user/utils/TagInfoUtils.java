package org.qiyu.live.user.utils;

/**
 * UserTag用户标签工具类
 */
public class TagInfoUtils {

    /**
     * 判断在tagInfo的二进制位中是否存在要匹配的标matchTag
     * @param tagInfo 数据库中存储的tag值
     * @param matchTag 要匹配是否存在该标签
     * @return
     */
    public static boolean isContain(Long tagInfo, Long matchTag) {
        return tagInfo != null && matchTag != null && matchTag != 0 && (tagInfo & matchTag) == matchTag;
    }
}
