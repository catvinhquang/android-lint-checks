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
import com.catvinhquang.androidlintchecks.Utils;
import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.impl.source.PsiParameterImpl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.UMethod;
import org.jetbrains.uast.UastCallKind;

import java.util.Arrays;
import java.util.List;

/**
 * Created by QuangCV on 24-Apr-2020
 **/

@SuppressWarnings("ALL")
public class FixedAnnotationDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "FixedAnnotationId", "", "",
            Category.CORRECTNESS, 1, Severity.FATAL,
            new Implementation(FixedAnnotationDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Arrays.asList(UMethod.class, UCallExpression.class);
    }

    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitMethod(@NotNull UMethod node) {
                JvmParameter[] params = node.getParameters();
                for (JvmParameter p : params) {
                    JvmAnnotation a = p.getAnnotation(Annotations.FIXED);
                    if (a == null) {
                        continue;
                    }

                    if (a instanceof PsiAnnotation) {
                        // check annotation attribute
                        PsiElement e = ((PsiAnnotation) a).findAttributeValue("value");
                        try {
                            PsiReferenceExpression expected = (PsiReferenceExpression) e;
                        } catch (Throwable t) {
                            t.printStackTrace();
                            context.report(ISSUE, e, context.getLocation(e),
                                    "Must be a reference expression");
                        }

                        // check parameter type
                        String type = null;
                        try {
                            type = ((PsiPrimitiveType) p.getType()).getName();
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        if (!"int".equals(type)) {
                            if (p instanceof PsiParameter) {
                                e = ((PsiParameterImpl) p).getTypeElement();
                                context.report(ISSUE, e, context.getLocation(e),
                                        "Only integer is supported");
                            }
                        }
                    }
                }
            }

            @Override
            public void visitCallExpression(@NotNull UCallExpression node) {
                if (node.getKind() == UastCallKind.METHOD_CALL) {
                    PsiMethod method = node.resolve();
                    if (method == null) {
                        return;
                    }

                    JvmParameter[] params = method.getParameters();
                    for (int i = 0; i < params.length; i++) {
                        try {
                            PsiAnnotation a = (PsiAnnotation) params[i].getAnnotation(Annotations.FIXED);
                            PsiReferenceExpression expected = (PsiReferenceExpression) a.findAttributeValue("value");
                            String expectedQualifiedName = Utils.getQualifiedName((PsiField) expected.resolve());
                            String expectValue = Utils.removePackageName(expectedQualifiedName, a.getProject());

                            UExpression e = node.getArgumentForParameter(i);
                            PsiReferenceExpression actual = null;
                            String actualQualifiedName = null;
                            try {
                                actual = (PsiReferenceExpression) e.getSourcePsi();
                                actualQualifiedName = Utils.getQualifiedName((PsiField) actual.resolve());
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }

                            if (actual == null || !expectedQualifiedName.equals(actualQualifiedName)) {
                                context.report(ISSUE, e, context.getLocation(e),
                                        "The value must be " + expectValue);
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        };
    }

}