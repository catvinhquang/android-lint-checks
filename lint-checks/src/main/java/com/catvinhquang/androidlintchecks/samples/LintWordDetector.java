package com.catvinhquang.androidlintchecks.samples;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Detector.UastScanner;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.ULiteralExpression;
import org.jetbrains.uast.UastLiteralUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by QuangCV on 21-Apr-2020
 *
 * Sample detector showing how to analyze Kotlin/Java code.
 * This example flags all string literals in the code that contain
 * the word "lint".
 **/

@SuppressWarnings("UnstableApiUsage")
public class LintWordDetector extends Detector implements UastScanner {

    /**
     * Issue describing the problem and pointing to the detector implementation
     */
    public static final Issue ISSUE = Issue.create(
            "SampleUniqueId", "",
            "Full explanation of the issue.\n" +
                    "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                    "When you modify this content, you need to reopen project to apply changes.",
            Category.CORRECTNESS, 6, Severity.FATAL,
            new Implementation(LintWordDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(ULiteralExpression.class);
    }

    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        // Note: Visiting UAST nodes is a pretty general purpose mechanism;
        // Lint has specialized support to do common things like "visit every class
        // that extends a given super class or implements a given interface", and
        // "visit every call site that calls a method by a given name" etc.
        // Take a careful look at UastScanner and the various existing lint check
        // implementations before doing things the "hard way".
        // Also be aware of context.getJavaEvaluator() which provides a lot of
        // utility functionality.
        return new UElementHandler() {
            @Override
            public void visitLiteralExpression(@NotNull ULiteralExpression expression) {
                String string = UastLiteralUtils.getValueIfStringLiteral(expression);
                if (string != null && string.toLowerCase().contains("lint")) {
                    context.report(ISSUE, expression,
                            context.getLocation(expression),
                            "The keyword has been found.");
                }
            }
        };
    }

}