package com.example.vknews

import android.content.Intent
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

    private val vkAuthCallback = object: VKIDAuthCallback {
        override fun onAuth(accessToken: com.vk.id.AccessToken) {
            Log.d("VKID", "Успешная авторизация: idToken=${accessToken.idToken}, scopes=${accessToken.scopes}")
            try {
                val tokenField = accessToken::class.java.getDeclaredField("token")
                tokenField.isAccessible = true
                val tokenValue = tokenField.get(accessToken)
                Log.d("VKID", "Token: $tokenValue")
                val intent = Intent(this@MainActivity, WallActivity::class.java)
                intent.putExtra("token", tokenValue.toString())
                startActivity(intent)
                finish()
            } catch (e: NoSuchFieldException) {
                Log.e("VKID", "Field token not found, using idToken")
                val intent = Intent(this@MainActivity, WallActivity::class.java)
                intent.putExtra("token", accessToken.idToken)
                startActivity(intent)
                finish()
            }
        }

        override fun onFail(fail: VKIDAuthFail) {
            Log.e("VKID", "Ошибка авторизации")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vkLoginButton.setOnClickListener {
            VKID.instance.authorize(
                lifecycleOwner = MainActivity@this,
                callback = vkAuthCallback,
                params = VKIDAuthParams {
                    scopes = setOf("wall", "vkid.personal_info") // для доступа к записям сообщества
                }
            )
        }
    }
}