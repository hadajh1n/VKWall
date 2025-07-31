package com.example.vknews

import android.os.Bundle
import android.view.View
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
        binding.progressBar.visibility = View.VISIBLE
        val groupId = -146026097
        val count = 50
        val accessToken = intent.getStringExtra("token") ?: return

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

                val json = JSONObject(result)
                val postsJson = json.getJSONObject("response").getJSONArray("items")
                val postList = mutableListOf<Post>()

                for (i in 0 until postsJson.length()) {
                    val post = postsJson.getJSONObject(i)
                    val text = post.optString("text", "")
                    val attachments = post.optJSONArray("attachments")
                    var imageUrl: String? = null

                    if (attachments != null) {
                        for (j in 0 until attachments.length()) {
                            val attachment = attachments.getJSONObject(j)
                            if (attachment.getString("type") == "photo") {
                                val photo = attachment.getJSONObject("photo")
                                val sizes = photo.getJSONArray("sizes")
                                if (sizes.length() > 0) {
                                    imageUrl = sizes.getJSONObject(sizes.length() - 1).getString("url")
                                    break
                                }
                            }
                        }
                    }

                    postList.add(Post(title = "Пост #${i + 1}", text = text, imageUrl = imageUrl, isEven = i % 2 == 0))
                }

                binding.recyclerView.adapter = PostAdapter(postList)

            } catch (e: Exception) {
                Toast.makeText(this@WallActivity, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}