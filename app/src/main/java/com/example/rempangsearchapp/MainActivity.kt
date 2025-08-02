package com.example.rempangsearchapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rempangsearchapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var historyList: MutableList<String>
    private lateinit var adapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        historyList = mutableListOf()

        val keywords = listOf(
            "rempang", "rempang eco city", "tanjung banun", "galang", 
            "sembulang", "sei buluh", "sei raya"
        )

        val baseUrl = "https://www.google.com/search?q="
        val filteredQueries = keywords.map {
            "$baseUrl${Uri.encode(it + " inurl:hari ini")}"
        }

        adapter = SearchAdapter(filteredQueries, this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }
        }
    }

    fun openUrl(url: String) {
        webView.loadUrl(url)
        historyList.add(url)
    }

    fun copyUrl(url: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied URL", url)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "URL disalin", Toast.LENGTH_SHORT).show()
    }

    fun shareUrl(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url)
        startActivity(Intent.createChooser(intent, "Bagikan URL"))
    }
}

class SearchAdapter(private val urls: List<String>, private val activity: MainActivity) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.urlText)
        val openBtn: Button = view.findViewById(R.id.openBtn)
        val copyBtn: Button = view.findViewById(R.id.copyBtn)
        val shareBtn: Button = view.findViewById(R.id.shareBtn)
        val dateText: TextView = view.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = urls[position]
        holder.title.text = Uri.parse(url).host ?: url
        holder.dateText.text = "2 hari yang lalu"

        holder.openBtn.setOnClickListener { activity.openUrl(url) }
        holder.copyBtn.setOnClickListener { activity.copyUrl(url) }
        holder.shareBtn.setOnClickListener { activity.shareUrl(url) }
    }
}
