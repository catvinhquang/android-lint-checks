package com.catvinhquang.androidlintchecks.detectors.samples

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.java.JavaUClass
import org.jetbrains.uast.kotlin.KotlinUClass
import java.util.*

/**
 * Created by QuangCV on 21-Apr-2020
 */

@Suppress("UnstableApiUsage")
class MissingAuthorNameDetector : Detector(), UastScanner {

    companion object {
        val ISSUE = Issue.create(
                "MissingAuthorName", "",
                "Full explanation of the issue.\n" +
                        "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                        "When you modify this content, you need to reopen project to apply changes.",
                CORRECTNESS, 1, Severity.FATAL,
                Implementation(MissingAuthorNameDetector::class.java, JAVA_FILE_SCOPE))
    }

    override fun getApplicableUastTypes(): List<Class<out UElement?>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                val match = ((node is JavaUClass || node is KotlinUClass)
                        && node.containingClass == null)
                if (match) {
                    var entered = false
                    var location: Location? = context.getNameLocation(node)
                    val comments = node.comments
                    if (comments.isNotEmpty()) {
                        location = context.getNameLocation(comments[0])
                        for (i in comments) {
                            val content = i.asSourceString().toLowerCase()
                            if (content.contains("created by")) {
                                entered = true
                                break
                            }
                        }
                    }
                    if (!entered) {
                        context.report(ISSUE, location!!, "Please enter author's name of this class.")
                    }
                }
            }
        }
    }

}