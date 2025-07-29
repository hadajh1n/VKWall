package com.example.vknews

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vknews.databinding.ActivityWallBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class WallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        fetchWallPosts()
    }

    private fun fetchWallPosts() {
        val groupId = -1
        val count = 50
        val accessToken = intent.getStringExtra("token") ?: run {
            Log.e("WallActivity", "Token is null")
            Toast.makeText(this, "Ошибка: токен отсутствует", Toast.LENGTH_LONG).show()
            return
        }
        val url = "https://api.vk.com/method/wall.get?owner_id=$groupId&count=$count&access_token=$accessToken&v=5.154"
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    response.body?.string() ?: throw IOException("Empty response")
                }
                Log.d("WallActivity", "API Response: $result")
                val json = JSONObject(result)
                if (json.has("error")) {
                    val error = json.getJSONObject("error")
                    val errorMsg = error.getString("error_msg")
                    val errorCode = error.getInt("error_code")
                    Log.e("WallActivity", "API Error: code=$errorCode, message=$errorMsg")
                    Toast.makeText(this@WallActivity, "Ошибка API: $errorMsg", Toast.LENGTH_LONG).show()
                    return@launch
                }
                val posts = json.getJSONObject("response").getJSONArray("items")
                val textList = mutableListOf<String>()
                for (i in 0 until posts.length()) {
                    val post = posts.getJSONObject(i)
                    val text = post.getString("text")
                    textList.add(text)
                }
                binding.recyclerView.adapter = PostAdapter(textList) // Используем PostAdapter
            } catch (e: Exception) {
                Log.e("WallActivity", "Error fetching posts", e)
                Toast.makeText(this@WallActivity, "Ошибка загрузки постов: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}