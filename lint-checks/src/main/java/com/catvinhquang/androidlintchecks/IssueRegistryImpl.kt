package com.catvinhquang.androidlintchecks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.catvinhquang.androidlintchecks.detectors.FixedAnnotationDetector
import com.catvinhquang.androidlintchecks.detectors.LargerThanZeroAnnotationDetector
import com.catvinhquang.androidlintchecks.detectors.NewThreadDetector
import com.catvinhquang.androidlintchecks.detectors.samples.CommentDetector
import com.catvinhquang.androidlintchecks.detectors.samples.MissingAuthorNameDetector
import com.catvinhquang.androidlintchecks.detectors.samples.SizeAnnotationDetector

/**
 * Created by QuangCV on 21-Apr-2020
 */

@Suppress("UnstableApiUsage")
class IssueRegistryImpl : IssueRegistry() {

    override val issues: List<Issue>
        get() = listOf(
                CommentDetector.ISSUE,
                MissingAuthorNameDetector.ISSUE,
                SizeAnnotationDetector.ISSUE,
                LargerThanZeroAnnotationDetector.ISSUE,
                NewThreadDetector.ISSUE,
                FixedAnnotationDetector.ISSUE)

    override val api: Int
        get() = CURRENT_API

}