package com.catvinhquang.androidlintchecks.detectors

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Created by QuangCV on 24-Apr-2020
 */

@Suppress("UnstableApiUsage")
class NewThreadDetector : Detector(), UastScanner {

    companion object {
        val ISSUE = Issue.create(
                "NewThreadId", "",
                "Please use Executor instead of Thread",
                CORRECTNESS, 1, Severity.ERROR,
                Implementation(NewThreadDetector::class.java, JAVA_FILE_SCOPE))
    }

    override fun getApplicableConstructorTypes(): List<String>? {
        return listOf("java.lang.Thread")
    }

    override fun visitConstructor(context: JavaContext,
                                  node: UCallExpression,
                                  constructor: PsiMethod) {
        context.report(ISSUE, node.uastParent, context.getLocation(node.uastParent!!),
                "Xin thí chủ hãy tự trọng, đừng tạo thêm nghiệp nữa 🤧",
                fix().name("Dạ! Em biết lỗi rồi, anh dạy bảo em đi 🍑")
                        .replace().all()
                        .with("executor.execute {\n" +
                                "            // TODO hót vô đây! 🦆\n" +
                                "\n" +
                                "        }")
                        .build())
    }

}