package com.huy.remoteconfigtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.huawei.agconnect.remoteconfig.AGConnectConfig
import com.huawei.agconnect.remoteconfig.ConfigValues
import kotlinx.android.synthetic.main.activity_main.*

private const val REMOTE_PARAMETER_KEY = "remote_parameter_key"

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var remoteConfig: AGConnectConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRemoteConfig()

        refreshConfig.setOnClickListener {
            fetchConfig()
        }
    }

    private fun initRemoteConfig() {
        remoteConfig = AGConnectConfig.getInstance()

        val defaultConfigMap = HashMap<String, Any>()
        defaultConfigMap[REMOTE_PARAMETER_KEY] = "Hello world!"

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
                        applyRemoteConfig(configValues)
                    }
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error fetching config: ", it)
                    textView.text = "No message from remote config server"
                }
    }

    private fun applyRemoteConfig(configValues: ConfigValues) {
        val value = configValues.getValueAsString(REMOTE_PARAMETER_KEY)
        textView.text = value
    }


}