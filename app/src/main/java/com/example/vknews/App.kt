package com.example.vknews

import android.app.Application
import com.vk.id.VKID
import java.util.Locale

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        VKID.init(this)
        VKID.instance.setLocale(Locale("ru"))
    }
}