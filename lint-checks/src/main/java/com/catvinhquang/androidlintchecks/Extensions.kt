package com.catvinhquang.androidlintchecks

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJvmMember
import com.intellij.psi.search.GlobalSearchScope
import org.apache.http.util.TextUtils
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.calls.callUtil.getCalleeExpressionIfAny
import org.jetbrains.uast.toUElement
import org.jetbrains.uast.tryResolve

/**
 * Created by QuangCV on 27-Apr-2020
 **/

fun PsiElement.resolve(): PsiElement? {
    return toUElement()?.tryResolve()
}

fun KtElement.resolve(): PsiElement? {
    return toUElement()?.tryResolve() ?: getCalleeExpressionIfAny()
}

fun PsiElement.getQualifiedName(packageNameIncluded: Boolean = false): String? {
    val resolved = resolve()
    var fqn = (resolved as? PsiJvmMember)
            ?.containingClass?.qualifiedName
            ?.plus(".")?.plus(resolved.name) ?: text

    if (packageNameIncluded) return fqn

    val facade = JavaPsiFacade.getInstance(project)
    val scope = GlobalSearchScope.allScope(project)
    var tmp = fqn

    var rootCls: PsiClass? = null
    var cls: PsiClass?
    do {
        tmp = tmp.substring(0, tmp.lastIndexOf("."))
        cls = facade.findClass(tmp, scope)
        if (cls != null) {
            rootCls = cls
        }
    } while (cls != null)

    if (rootCls != null) {
        val rootQN = rootCls.qualifiedName
        if (!TextUtils.isEmpty(rootQN)) {
            fqn = fqn.substring(rootQN!!.lastIndexOf(".") + 1)
        }
    }
    return fqn
}