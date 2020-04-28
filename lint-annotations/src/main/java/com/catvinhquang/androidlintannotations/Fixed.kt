package com.catvinhquang.androidlintannotations

/**
 * Created by QuangCV on 24-Apr-2020
 */

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Fixed(val value: Int)