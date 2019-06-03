package droid.smart.com.tamilkuripugal.extensions

import android.webkit.WebView
import com.crashlytics.android.Crashlytics
import droid.smart.com.tamilkuripugal.vo.Kurippu
import org.jsoup.Jsoup


fun WebView.loadKurippu(kurippu: Kurippu) {
    try {
        //Timber.d("Kurippu => %s", kurippu.content)
        val doc = Jsoup.parse(kurippu.content)
        val image = doc.select("img")
        doc.select("a").remove()

        val body = doc.select("body").first()
        body.prependElement("img")
            .attr("src", image.attr("src"))
            .attr("width", image.attr("width"))
            .attr("height", image.attr("height"))
            .attr("style", "display:block; margin-left: auto; margin-right: auto; margin-bottom: 20px;")

        //Timber.d("Kurippu Processed => %s", doc.html())
        this.loadKurippu(doc.html())
    } catch (ex: Exception) {
        Crashlytics.logException(ex)
        this.loadKurippu(kurippu.content!!)
    }
}


fun WebView.loadKurippu(htmlContent: String) {
    this.loadData(htmlContent, "text/html", null)
}
