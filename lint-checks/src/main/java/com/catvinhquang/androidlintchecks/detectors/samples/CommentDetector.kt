package com.catvinhquang.androidlintchecks.detectors.samples

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.evaluateString
import org.jetbrains.uast.getValueIfStringLiteral
import java.util.*

/**
 * Created by QuangCV on 21-Apr-2020
 *
 * Sample detector showing how to analyze Kotlin/Java code.
 * This example flags all string literals in the code that contain
 * the word "lint".
 */

@Suppress("UnstableApiUsage")
class CommentDetector : Detector(), UastScanner {

    companion object {
        val ISSUE = Issue.create(
                "SampleUniqueId", "",
                "Full explanation of the issue.\n" +
                        "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                        "When you modify this content, you need to reopen project to apply changes.",
                CORRECTNESS, 1, Severity.FATAL,
                Implementation(CommentDetector::class.java, JAVA_FILE_SCOPE))
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(ULiteralExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        // Note: Visiting UAST nodes is a pretty general purpose mechanism;
        // Lint has specialized support to do common things like "visit every class
        // that extends a given super class or implements a given interface", and
        // "visit every call site that calls a method by a given name" etc.
        // Take a careful look at UastScanner and the various existing lint check
        // implementations before doing things the "hard way".
        // Also be aware of context.getJavaEvaluator() which provides a lot of
        // utility functionality.
        return object : UElementHandler() {
            override fun visitLiteralExpression(node: ULiteralExpression) {
                val string = node.evaluateString()
                if (string != null && string.toLowerCase(Locale.getDefault()).contains("hello world")) {
                    context.report(ISSUE, node,
                            context.getLocation(node),
                            "The keyword has been found.",
                            fix().replace().all().with("test quick fix").build())
                }
            }
        }
    }

}