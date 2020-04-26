package com.catvinhquang.androidlintchecks;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.search.GlobalSearchScope;

import org.apache.http.util.TextUtils;

/**
 * Created by QuangCV on 26-Apr-2020
 **/

public class Utils {

    public static String getQualifiedName(PsiField f) {
        String result = null;
        try {
            PsiClass c = f.getContainingClass();
            if (c != null) {
                result = c.getQualifiedName() + "." + f.getName();
            } else {
                result = f.getName();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public static String removePackageName(String qualifiedName, Project p) {
        String tmp = qualifiedName;
        JavaPsiFacade facade = JavaPsiFacade.getInstance(p);
        GlobalSearchScope scope = GlobalSearchScope.allScope(p);

        PsiClass c;
        PsiClass root = null;
        do {
            tmp = tmp.substring(0, tmp.lastIndexOf("."));
            c = facade.findClass(tmp, scope);
            if (c != null) {
                root = c;
            }
        } while (c != null);

        if (root != null) {
            String rootQN = root.getQualifiedName();
            if (!TextUtils.isEmpty(rootQN)) {
                qualifiedName = qualifiedName.substring(rootQN.lastIndexOf(".") + 1);
            }
        }
        return qualifiedName;
    }

}