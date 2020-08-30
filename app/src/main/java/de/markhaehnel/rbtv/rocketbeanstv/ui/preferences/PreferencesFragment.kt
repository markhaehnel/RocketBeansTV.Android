package de.markhaehnel.rbtv.rocketbeanstv.ui.preferences

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import de.markhaehnel.rbtv.rocketbeanstv.R


class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        setPrivacyPolicyClickListener()
    }

    private fun setPrivacyPolicyClickListener() {
        val privacyPolicyPreference: Preference? = findPreference("privacypolicy")
        privacyPolicyPreference?.setOnPreferenceClickListener {
            val alert = AlertDialog.Builder(requireContext())

            val wv = WebView(requireContext()).apply {
                loadUrl("https://sites.google.com/view/rbtv-firetv-privacy-policy")
                settings.apply {
                    @SuppressLint("SetJavaScriptEnabled")
                    //javaScriptEnabled = true
                    useWideViewPort = true
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.scrollTo(0, 0)
                    }
                }
            }

            alert.apply {
                setView(wv)
                show()
            }

            true
        }
    }
}

