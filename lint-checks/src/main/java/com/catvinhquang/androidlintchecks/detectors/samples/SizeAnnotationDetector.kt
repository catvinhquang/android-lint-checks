package com.catvinhquang.androidlintchecks.detectors.samples

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UastCallKind

/**
 * Created by QuangCV on 21-Apr-2020
 */

@Suppress("UnstableApiUsage")
class SizeAnnotationDetector : Detector(), UastScanner {

    companion object {
        val ISSUE = Issue.create(
                "SizeAnnotationId", "",
                "Full explanation of the issue.\n" +
                        "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                        "When you modify this content, you need to reopen project to apply changes.",
                CORRECTNESS, 1, Severity.FATAL,
                Implementation(SizeAnnotationDetector::class.java, JAVA_FILE_SCOPE))
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
                        val ja = params[i].getAnnotation("androidx.annotation.Size")
                        if (ja is PsiAnnotationImpl) {
                            val pa = ja
                            val value = getAttributeValue(pa, "value")
                            val min = getAttributeValue(pa, "min")
                            val max = getAttributeValue(pa, "max")
                            val multiple = getAttributeValue(pa, "multiple")
                            val e = node.getArgumentForParameter(i)
                            context.report(ISSUE, e, context.getLocation(e!!), String.format("@Size (value = %1\$d, min = %2\$d, max = %3\$d, multiple = %4\$d) has been found.",
                                    value, min, max, multiple))
                        }
                    }
                }
            }
        }
    }

    private fun getAttributeValue(annotation: PsiAnnotationImpl,
                                  attributeName: String): Long {
        return annotation.findAttributeValue(attributeName)
                ?.text?.replace("L", "")?.toLong() ?: 0
    }

}