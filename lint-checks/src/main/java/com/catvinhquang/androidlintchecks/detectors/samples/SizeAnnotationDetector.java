package com.catvinhquang.androidlintchecks.detectors.samples;

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
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.UastCallKind;

import java.util.Collections;
import java.util.List;

/**
 * Created by QuangCV on 21-Apr-2020
 **/

@SuppressWarnings("ALL")
public class SizeAnnotationDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "SizeAnnotationId", "",
            "Full explanation of the issue.\n" +
                    "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                    "When you modify this content, you need to reopen project to apply changes.",
            Category.CORRECTNESS, 1, Severity.FATAL,
            new Implementation(SizeAnnotationDetector.class, Scope.JAVA_FILE_SCOPE));

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
                        JvmAnnotation ja = params[i].getAnnotation("androidx.annotation.Size");
                        if (ja instanceof PsiAnnotationImpl) {
                            PsiAnnotationImpl pa = (PsiAnnotationImpl) ja;
                            long value = getAttributeValue(pa, "value");
                            long min = getAttributeValue(pa, "min");
                            long max = getAttributeValue(pa, "max");
                            long multiple = getAttributeValue(pa, "multiple");

                            UExpression e = node.getArgumentForParameter(i);
                            context.report(ISSUE, e, context.getLocation(e),
                                    String.format("@Size (value = %1$d, min = %2$d, max = %3$d, multiple = %4$d) has been found.",
                                            value, min, max, multiple));
                        }
                    }
                }
            }
        };
    }

    private long getAttributeValue(PsiAnnotationImpl annotation,
                                   String attributeName) {
        long result = 0;
        try {
            result = Long.parseLong(
                    annotation.findAttributeValue(attributeName)
                            .getText()
                            .replace("L", "")
            );
        } catch (Throwable ignore) {
        }
        return result;
    }

}