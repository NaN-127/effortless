package com.effortless.app

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.effortless.core.network.AndroidNetworkConfig

class EffortlessApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initNetworkDebugging()
    }

    private fun initNetworkDebugging() {
        // Configure Chucker for HTTP inspection with strict privacy and secret redaction
        val chuckerCollector = ChuckerCollector(
            context = this,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        val chuckerInterceptor = ChuckerInterceptor.Builder(this)
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .redactHeaders("Authorization", "X-API-Key", "Cookie", "Set-Cookie")
            .alwaysReadResponseBody(false)
            .build()

        AndroidNetworkConfig.addInterceptor(chuckerInterceptor)
    }
}
