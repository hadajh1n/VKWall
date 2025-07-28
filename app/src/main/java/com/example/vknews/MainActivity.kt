package com.example.vknews

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.vknews.databinding.ActivityMainBinding
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.VKIDAuthCallback
import com.vk.id.auth.VKIDAuthParams

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    val vkAuthCallback = object: VKIDAuthCallback {
        override fun onAuth(accessToken: AccessToken) {
            Log.d("VKID", "Успешная авторизация: ${accessToken.idToken}")
        }


        override fun onFail(fail: VKIDAuthFail) {
            Log.e("VKID", "Ошибка авторизации")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        VKID.instance.authorize(
            lifecycleOwner = MainActivity@this,
            callback = vkAuthCallback,
            params = VKIDAuthParams {
                scopes = setOf("status", "email")
            }
        )
    }
}