package droid.smart.com.tamilkuripugal.ui.util

import android.content.Context
import android.content.res.Resources
import timber.log.Timber

class Helper {

    companion object Formatter {
        fun localeText(ctx: Context, paramString: String?): String? {
            var prmString = paramString
            var retValue = prmString
            if (prmString != null && !prmString.equals("", ignoreCase = true)) {
                retValue = prmString.trim { it <= ' ' }
                prmString = prmString.replace(" ", "_")
                try {
                    val resId = ctx.resources.getIdentifier(prmString, "string", ctx.packageName)
                    retValue = ctx.getString(resId)
                } catch (e: Resources.NotFoundException) {
                    Timber.w("Resource missing for  : %s", prmString)
                }
            }
            return retValue
        }
    }
}