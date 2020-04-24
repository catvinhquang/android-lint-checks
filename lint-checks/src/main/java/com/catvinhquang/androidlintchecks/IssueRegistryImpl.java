package com.catvinhquang.androidlintchecks;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;
import com.catvinhquang.androidlintchecks.detectors.LargerThanZeroAnnotationDetector;
import com.catvinhquang.androidlintchecks.detectors.NewThreadDetector;
import com.catvinhquang.androidlintchecks.detectors.samples.CommentDetector;
import com.catvinhquang.androidlintchecks.detectors.samples.MissingAuthorNameDetector;
import com.catvinhquang.androidlintchecks.detectors.samples.SizeAnnotationDetector;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Created by QuangCV on 21-Apr-2020
 **/

@SuppressWarnings("UnstableApiUsage")
public class IssueRegistryImpl extends IssueRegistry {

    @NotNull
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(CommentDetector.ISSUE,
                MissingAuthorNameDetector.ISSUE,
                SizeAnnotationDetector.ISSUE,
                LargerThanZeroAnnotationDetector.ISSUE,
                NewThreadDetector.ISSUE);
    }

    @Override
    public int getApi() {
        return ApiKt.CURRENT_API;
    }

}