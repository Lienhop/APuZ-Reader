package com.apuzreader

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.apuzreader.ui.theme.ApuzReaderTheme
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import com.prof18.rssparser.model.RssChannel
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.util.saveTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.CompletableFuture
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.apuzreader.ui.LoadingScreen
import com.apuzreader.ui.MainScreen
import com.apuzreader.models.ApuzData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val apuzRssPdf = "https://www.bpb.de/rss-feed/230868.rss"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val apuzList = remember { mutableStateOf(loadApuzList()) }
            val isLoading = remember { mutableStateOf(true) }
            val drawerState = rememberDrawerState(DrawerValue.Closed)

            LaunchedEffect(Unit) {
                apuzList.value = getApuzEditions(apuzRssPdf)
                isLoading.value = false
            }


            ApuzReaderTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { paddingValues ->
                    if (isLoading.value) {
                        LoadingScreen(modifier = Modifier.padding(paddingValues))
                    } else {
                        MainScreen(
                            pdfList = apuzList.value,
                            onLaunchPdf = { url, title -> launchPdfFromUrl(url, title) },
                            onToggleFavorite = { apuzData ->
                                val updatedList = apuzList.value.map {
                                    if (it.title == apuzData.title) it.copy(isFavorite = !it.isFavorite) else it
                                }
                                apuzList.value = updatedList
                                saveApuzList(updatedList)
                            },
                            drawerState = drawerState
                        )
                    }
                }
            }
        }
    }

    private fun saveApuzList(list: List<ApuzData>) {
        val sharedPrefs = getSharedPreferences("ApuzReaderPrefs", MODE_PRIVATE)
        val json = Gson().toJson(list)
        sharedPrefs.edit().putString("apuzList", json).apply()
    }

    private fun loadApuzList(): List<ApuzData> {
        val sharedPrefs = getSharedPreferences("ApuzReaderPrefs", MODE_PRIVATE)
        val json = sharedPrefs.getString("apuzList", null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<List<ApuzData>>() {}.type)
        } else {
            emptyList()
        }
    }

    private fun parseFeed(parser: RssParser, url: String): CompletableFuture<RssChannel> = GlobalScope.future {
        parser.getRssChannel(url)
    }

    private fun getImgAndUrl(pageSrc: String): Pair<String, String> {
        val doc: Document = Ksoup.parse(html = pageSrc)
        val metaTags = doc.getElementsByTag("meta")
        var img = ""
        var url = ""
        for (tag in metaTags) {
            if (tag.attr("property") == "og:image") {
                img = tag.attr("content")
            }
        }
        val urlTags = doc.getElementsByTag("bpb-input-radio")
        for (tag in urlTags) {
            if (tag.attr("name") == "download-url") {
               url = "https://www.bpb.de" + tag.attr("value").substringBefore("?")
            }
        }
        Log.e("KSoup", "Title: ${doc.title()}")
        return Pair(img, url)
    }

    private suspend fun getPageSource(url: String): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        response.body?.string() ?: ""
    }

    private suspend fun getApuzEditions(urlString: String): List<ApuzData> {
        val parser = RssParserBuilder().build()
        val existingList = loadApuzList().toMutableList()
        try {
            val channel: RssChannel = parseFeed(parser, urlString).get()
            for (item in channel.items) {
                val title = item.title ?: ""
                if (existingList.none { it.title == title }) {
                    val src = item.link.toString()
                    val pageSrc = getPageSource(src)
                    val imgUrl = getImgAndUrl(pageSrc)
                    Log.e("DATE", parseDate(item.pubDate.toString()).toString())
                    val apuzData = ApuzData(
                        title = title,
                        description = item.description ?: "",
                        date = parseDate(item.pubDate.toString()),
                        //date = LocalDate.now(),
                        imageUrl = imgUrl.first,
                        pdfUrl = imgUrl.second
                    )
                    println(apuzData)
                    existingList.add(apuzData)
                }
            }
            saveApuzList(existingList)
            Log.e("ApuzData", existingList.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return existingList
    }

    fun parseDate(dateString: String?): LocalDate? {
        if (dateString.isNullOrEmpty()) return null
        val trimmedDateString = dateString.dropLast(15).drop(5)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        return LocalDate.parse(trimmedDateString, formatter)
    }


    private fun launchPdfFromUrl(url: String, title: String) {
        startActivity(
            PdfViewerActivity.launchPdfFromUrl(
                context = this,
                pdfUrl = url,
                pdfTitle = title,
                saveTo = saveTo.ASK_EVERYTIME,
                enableDownload = true
            )
        )
    }
}