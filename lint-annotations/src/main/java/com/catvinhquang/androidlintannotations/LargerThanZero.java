package com.catvinhquang.androidlintannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by QuangCV on 22-Apr-2020
 **/

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER})
public @interface LargerThanZero {
}