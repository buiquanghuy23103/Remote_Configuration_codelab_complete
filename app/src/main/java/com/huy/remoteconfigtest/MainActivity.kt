package com.huy.remoteconfigtest

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.remoteconfig.AGConnectConfig
import com.huawei.agconnect.remoteconfig.ConfigValues
import kotlinx.android.synthetic.main.activity_main.*

private const val ANIMATION_URL_KEY = "animation_url_key"

private const val ERROR_ANIMATION_JSON = "error.json"

class MainActivity : AppCompatActivity() {

    private lateinit var remoteConfig: AGConnectConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRemoteConfig()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh_config -> {
                fetchConfig()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initRemoteConfig() {
        remoteConfig = AGConnectConfig.getInstance()

        val defaultConfigMap = HashMap<String, Any>()
        defaultConfigMap[ANIMATION_URL_KEY] = ERROR_ANIMATION_JSON

        remoteConfig.applyDefault(defaultConfigMap)

        if (BuildConfig.DEBUG) {
            remoteConfig.setDeveloperMode(true)
        }

        fetchConfig()
    }

    private fun fetchConfig() {
        val cacheExpirationInSeconds = if (BuildConfig.DEBUG) 0 else 3600L

        remoteConfig.fetch(cacheExpirationInSeconds)
                .addOnSuccessListener {
                    it?.let { configValues ->
                        remoteConfig.apply(configValues)
                        applyAnimationFromRemoteConfig(configValues)
                    }
                }
                .addOnFailureListener {
                    showErrorLog("Error fetching remote parameter: ${it.localizedMessage}")
                }
    }

    private fun applyAnimationFromRemoteConfig(configValues: ConfigValues) {
        val animationUrl = configValues.getValueAsString(ANIMATION_URL_KEY)
        if (!animationUrl.isNullOrEmpty()) {
            hideErrorView()
            animation_view.setAnimationFromUrl(animationUrl)
        } else {
            showErrorLog("animationUrl is empty")
        }
    }

    private fun showErrorLog(errorMessage: String) {
        animation_view.visibility = View.GONE
        error_view.visibility = View.VISIBLE
        error_message.text = errorMessage
    }

    private fun hideErrorView() {
        animation_view.visibility = View.VISIBLE
        error_view.visibility = View.GONE
    }

}