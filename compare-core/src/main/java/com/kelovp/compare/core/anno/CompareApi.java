package com.kelovp.compare.core.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author KelovpString
 */
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompareApi {

    /**
     * 当前方法名，不允许重复，用于区分不同方法
     */
    String name();

    /**
     * 需排除比较的字段 仅支持string
     */
    String[] excludeFields() default { "" };

}
