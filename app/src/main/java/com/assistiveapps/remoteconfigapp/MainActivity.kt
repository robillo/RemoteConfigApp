package com.assistiveapps.remoteconfigapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var defaultConfigMap: MutableMap<String, Any>

    companion object {
        const val TEXT_PARAMETER_NAME = "text_to_display"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()
    }

    private fun setup() {
        initRemoteConfig()
        setDefaultsToConfig()
        fetchConfigurations()
        setClickListeners()
    }

    private fun setClickListeners() {
        changeDisplayTextBtn.setOnClickListener {
            mFirebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            Log.e("tag", "Config params updated: ${it.result}")
                            Toast.makeText(this, "Fetch and activate succeeded", Toast.LENGTH_SHORT).show()
                            applyUpdatedText()
                        }
                        else {
                            Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            applyUpdatedText()
        }
    }

    private fun initRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true).build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun setDefaultsToConfig() {
        defaultConfigMap = HashMap()
        defaultConfigMap.put(TEXT_PARAMETER_NAME, "This is default set text")
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap)
        mFirebaseRemoteConfig.activate()
    }

    private fun fetchConfigurations() {
//        val cacheExpiration = 3600L //1 hour

        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener {

                if(it.isSuccessful) {
                    Log.e("tag", "successful")
                }
                else {
                    Log.e("tag", "not successful")
                }

                applyUpdatedText()

            }.addOnFailureListener {
                Log.e("tag", "failure")
            }.addOnCanceledListener {
                Log.e("tag", "cancelled")
            }
    }

    private fun applyUpdatedText() {
        val textToDisplay = mFirebaseRemoteConfig.getString(TEXT_PARAMETER_NAME)
        textToDisplayTv.text = textToDisplay
    }
}
