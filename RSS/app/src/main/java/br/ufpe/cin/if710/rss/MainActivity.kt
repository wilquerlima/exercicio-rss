package br.ufpe.cin.if710.rss

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {



    //ao fazer envio da resolucao, use este link no seu codigo!
    //private val RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml"

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome
    private var recycleView: RecyclerView? = null
    private var toolbar: Toolbar? = null
    private var adapterRecycleView: RecycleViewAdapter = RecycleViewAdapter(emptyList(), this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycleView = findViewById(R.id.conteudoRSS)

        //atribuindo o elemento do xml ao codigo
        toolbar = findViewById(R.id.toolbar)
        //setando a toolbar à activity
        setSupportActionBar(toolbar)
        //adicionando o botao na toolbar
        supportActionBar?.setHomeButtonEnabled(true)

        //configurando as propriedades da recyclerview informando o seu
        // respectivo layout manager e adapter
        recycleView?.layoutManager = LinearLayoutManager(applicationContext)
        recycleView?.adapter = adapterRecycleView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_edit -> {
                val intent = Intent(this, ChangeRSSActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //doAsync para realizar downloads fora da main thread
        doAsync {
            try {
                val feedXML = getRssFeed(getRSSPreferences())
                val listParser = ParserRSS.parse(feedXML)

                //é preciso rodar o notify dentro da uithread pq apenas ela pode alterar a view
                uiThread {
                    adapterRecycleView.listItens = listParser
                    //notificar o adapter que foi alterado os seus elementos
                    adapterRecycleView.notifyDataSetChanged()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    //Opcional - pesquise outros meios de obter arquivos da internet - bibliotecas, etc.
    @Throws(IOException::class)
    private fun getRssFeed(feed: String): String {
        var ipt: InputStream? = null
        var rssFeed = ""
        try {
            val url = URL(feed)
            val conn = url.openConnection() as HttpURLConnection
            ipt = conn.inputStream
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count: Int = ipt.read(buffer)
            while (count != -1) {
                out.write(buffer, 0, count)
                count = ipt.read(buffer)
            }
            val response = out.toByteArray()
            rssFeed = String(response, Charset.forName("UTF-8"))
        } finally {
            ipt?.close()
        }
        return rssFeed
    }

    private fun getRSSPreferences(): String {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val urlRSS = preferences.getString(RSSFEED, "http://leopoldomt.com/if1001/g1brasil.xml")

        return urlRSS
    }

    companion object {
        private val RSSFEED = "rssFeed"
    }
}