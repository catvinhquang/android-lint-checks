package com.catvinhquang.androidlintchecks.detectors;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiMethod;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UastCallKind;

import java.util.Collections;
import java.util.List;

/**
 * Created by QuangCV on 21-Apr-2020
 **/

@SuppressWarnings("ALL")
public class LargerThanZeroAnnotationDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "LargerThanZeroAnnotationId", "",
            "Full explanation of the issue.\n" +
                    "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                    "When you modify this content, you need to reopen project to apply changes.",
            Category.CORRECTNESS, 1, Severity.FATAL,
            new Implementation(LargerThanZeroAnnotationDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(UCallExpression.class);
    }

    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitCallExpression(@NotNull UCallExpression node) {
                if (node.getKind() == UastCallKind.METHOD_CALL) {
                    PsiMethod method = node.resolve();
                    if (method == null) {
                        return;
                    }

                    JvmParameter[] params = method.getParameters();
                    for (int i = 0; i < params.length; i++) {
                        JvmAnnotation a = params[i].getAnnotation("com.catvinhquang.androidlintannotations.LargerThanZero");
                        if (a != null) {
                            Object value = node.getValueArguments().get(i).evaluate();
                            if (value instanceof Integer && (Integer) value <= 0) {
                                context.report(ISSUE, node, context.getLocation(node), "The value must be larger than zero");
                            }
                        }
                    }
                }
            }
        };
    }

}