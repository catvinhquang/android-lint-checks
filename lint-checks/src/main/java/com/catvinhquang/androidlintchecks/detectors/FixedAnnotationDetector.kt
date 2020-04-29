package com.catvinhquang.androidlintchecks.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.catvinhquang.androidlintchecks.Annotations
import com.catvinhquang.androidlintchecks.getQualifiedName
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiField
import com.intellij.psi.PsiPrimitiveType
import org.jetbrains.uast.*

/**
 * Created by QuangCV on 24-Apr-2020
 */

@Suppress("UnstableApiUsage")
class FixedAnnotationDetector : Detector(), UastScanner {

    companion object {
        val ISSUE: Issue = Issue.create(
                "FixedAnnotation", "@Fixed", "Nothing",
                CORRECTNESS, 1, Severity.FATAL,
                Implementation(FixedAnnotationDetector::class.java, JAVA_FILE_SCOPE))
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UMethod::class.java, UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitMethod(node: UMethod) {
                val params = node.parameters
                for (p in params) {
                    val a = p.getAnnotation(Annotations.FIXED)
                            as? PsiAnnotation ?: continue

                    // check annotation attribute
                    val v = a.findAttributeValue("value")
                    if (v != null) {
                        val e = v.toUElement()?.tryResolve()
                        if (e !is PsiField) {
                            context.report(ISSUE, v, context.getLocation(v),
                                    "Reference expression expected")
                        }
                    }

                    // check parameter type
                    val type = (p.type as? PsiPrimitiveType)?.name
                    if ("int" != type) {
                        context.report(ISSUE, a, context.getLocation(a),
                                "This annotation applies only to type int")
                    }
                }
            }

            override fun visitCallExpression(node: UCallExpression) {
                if (node.kind === UastCallKind.METHOD_CALL) {
                    val method = node.resolve() ?: return
                    val params = method.parameters
                    for (i in params.indices) {
                        val a = params[i].getAnnotation(Annotations.FIXED)
                                as? PsiAnnotation ?: continue

                        val expectedQN = a.findAttributeValue("value")?.getQualifiedName(true)
                        val expectValue = a.findAttributeValue("value")?.getQualifiedName()

                        val e = node.getArgumentForParameter(i)
                        val actualQN = e?.sourcePsi?.getQualifiedName(true)

                        if (e != null && expectedQN != actualQN) {
                            context.report(ISSUE, e, context.getLocation(e),
                                    "The value must be $expectValue")
                        }
                    }
                }
            }
        }
    }
}