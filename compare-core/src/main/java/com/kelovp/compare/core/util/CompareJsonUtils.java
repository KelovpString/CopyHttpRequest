package com.kelovp.compare.core.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.*;
import com.kelovp.compare.core.bean.CompareResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author KelovpString
 */
@Slf4j
public class CompareJsonUtils {
    public static boolean compareTwoJsonObject(JSONObject jo1, JSONObject jo2, CompareResult compareResult,
                                               String... excludeFields) {
        try {
            String os1 = filterExcludeFields(jo1, excludeFields);
            String os2 = filterExcludeFields(jo2, excludeFields);

            compareResult.setSourceStr(os1);
            compareResult.setCopyStr(os2);

            return os1.equals(os2);
        } catch (Exception e) {
            log.error("", e);
        }
        return false;
    }

    /**
     * 过滤不需要验证的字段，值替换为字段本身，目前只支持字符串类型
     *
     * @param obj JSON对象
     * @param excludeFields 需过滤的字段
     * @return String
     */
    private static String filterExcludeFields(JSONObject obj, String... excludeFields) {
        String filterJsonStr = JSONObject.toJSONString(obj, SerializerFeature.SortField,SerializerFeature.MapSortField);
        if (excludeFields == null || excludeFields.length == 0) {
            return filterJsonStr;
        }

        PropertyFilter filter = new PropertyFilter() {
            @Override
            public boolean apply(Object o, String s, Object o1) {
                return !Arrays.asList(excludeFields).contains(s);
            }
        };

        return  JSONObject.toJSONString(obj,filter,SerializerFeature.SortField,SerializerFeature.MapSortField);
    }
}
