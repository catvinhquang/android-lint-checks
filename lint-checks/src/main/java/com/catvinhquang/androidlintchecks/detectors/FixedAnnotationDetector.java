package com.catvinhquang.androidlintchecks.detectors;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.catvinhquang.androidlintchecks.Annotations;
import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiFieldImpl;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.UastCallKind;

import java.util.Collections;
import java.util.List;

/**
 * Created by QuangCV on 24-Apr-2020
 **/

@SuppressWarnings("ALL")
public class FixedAnnotationDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "FixedAnnotationId", "",
            "Full explanation of the issue.\n" +
                    "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                    "When you modify this content, you need to reopen project to apply changes.",
            Category.CORRECTNESS, 1, Severity.FATAL,
            new Implementation(FixedAnnotationDetector.class, Scope.JAVA_FILE_SCOPE));

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
                        JvmAnnotation ja = params[i].getAnnotation(Annotations.FIXED);
                        if (ja instanceof PsiAnnotationImpl) {
                            PsiAnnotationImpl pa = (PsiAnnotationImpl) ja;
                            int expectedValue = getAttributeValue(pa, "value");

                            UExpression e = node.getArgumentForParameter(i);
                            if (e != null) {
                                Object o = e.evaluate();
                                if (o instanceof Integer && (Integer) o != expectedValue) {
                                    context.report(ISSUE, e, context.getLocation(e),
                                            String.format("The value must be equal to %d.", expectedValue));
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private int getAttributeValue(PsiAnnotationImpl annotation,
                                  String attributeName) {
        int result = 0;
        try {
            result = Integer.parseInt(
                    annotation.findAttributeValue(attributeName)
                            .getText()
            );
        } catch (Throwable t1) {
            try {
                result = (int) ((PsiFieldImpl) ((PsiReferenceExpressionImpl) annotation.findAttributeValue("value")).resolve())
                        .computeConstantValue();
            } catch (Throwable t2) {
            }
        }
        return result;
    }

}