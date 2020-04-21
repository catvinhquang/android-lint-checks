package com.catvinhquang.androidlintchecks.samples;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UComment;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.java.JavaUClass;
import org.jetbrains.uast.kotlin.KotlinUClass;

import java.util.Collections;
import java.util.List;

/**
 * Created by QuangCV on 21-Apr-2020
 **/

@SuppressWarnings("UnstableApiUsage")
public class MissingAuthorNameDetector extends Detector implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create(
            "MissingAuthorName", "",
            "Full explanation of the issue.\n" +
                    "You can use some markdown markup such as `monospace`, *italic* and **bold**.\n" +
                    "When you modify this content, you need to reopen project to apply changes.",
            Category.CORRECTNESS, 6, Severity.FATAL,
            new Implementation(MissingAuthorNameDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(UClass.class);
    }

    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitClass(@NotNull UClass node) {
                boolean match = (node instanceof JavaUClass || node instanceof KotlinUClass)
                        && node.getContainingClass() == null;
                if (match) {
                    boolean entered = false;
                    Location location = context.getNameLocation(node);

                    List<UComment> comments = node.getComments();
                    if (!comments.isEmpty()) {
                        location = context.getNameLocation(comments.get(0));
                        for (UComment i : comments) {
                            String content = i.asSourceString().toLowerCase();
                            if (content.contains("created by")) {
                                entered = true;
                                break;
                            }
                        }
                    }

                    if (!entered) {
                        context.report(ISSUE, location, "Please enter author's name of this class.");
                    }
                }
            }
        };
    }

}