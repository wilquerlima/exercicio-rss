package br.ufpe.cin.if710.rss

import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat

class ChangeRSSActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Carrega um layout que contem um fragmento
        setContentView(R.layout.activity_edit_rss)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment, ChangeRSSFragment())
                .commit()
    }

    class ChangeRSSFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            //adicionando o arquivo xml de preferencias
            addPreferencesFromResource(R.xml.preferences)
        }

        private var mListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
        private var mRSSPreference: Preference? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            //pegando as preferencias
            mRSSPreference = preferenceManager.findPreference(RSSFEED)

            mListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                mRSSPreference!!.summary = sharedPreferences.getString(
                        RSSFEED, "http://leopoldomt.com/if1001/g1brasil.xml")
            }

            // obtem as preferencias e registrando o listener
            val preferences = preferenceManager.sharedPreferences
            preferences.registerOnSharedPreferenceChangeListener(mListener)
            // Salva as mudanças do RSS nas preferencias
            mListener!!.onSharedPreferenceChanged(preferences, RSSFEED)
        }

    }

    companion object {
        private val RSSFEED = "rssFeed"
    }

}