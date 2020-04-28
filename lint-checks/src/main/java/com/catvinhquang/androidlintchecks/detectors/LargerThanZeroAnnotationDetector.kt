package com.catvinhquang.androidlintchecks.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UastCallKind

/**
 * Created by QuangCV on 21-Apr-2020
 */

@Suppress("UnstableApiUsage")
class LargerThanZeroAnnotationDetector : Detector(), UastScanner {

    companion object {
        val ISSUE = Issue.create(
                "LargerThanZeroAnnotationId", "",
                "Full explanation of the issue.\n" +
                        "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                        "When you modify this content, you need to reopen project to apply changes.",
                CORRECTNESS, 1, Severity.FATAL,
                Implementation(LargerThanZeroAnnotationDetector::class.java, JAVA_FILE_SCOPE))
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                if (node.kind === UastCallKind.METHOD_CALL) {
                    val method = node.resolve() ?: return
                    val params = method.parameters
                    for (i in params.indices) {
                        val a = params[i].getAnnotation("com.catvinhquang.androidlintannotations.LargerThanZero")
                        if (a != null) {
                            val value = node.valueArguments[i].evaluate()
                            if (value is Int && value <= 0) {
                                context.report(ISSUE, node, context.getLocation(node), "The value must be larger than zero")
                            }
                        }
                    }
                }
            }
        }
    }

}