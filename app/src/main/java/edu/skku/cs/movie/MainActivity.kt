package edu.skku.cs.movie

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

private var requestToken: String? = null

class MainActivity : AppCompatActivity() {
    String apiKey = BuildConfig.api_key;
    String AUTH_TOKEN = BuildConfig.auth_token;
    companion object{
        const val EXT_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.load(R.drawable.main)
        val trend = findViewById<ImageButton>(R.id.trend)
        trend.load(R.drawable.logo_trend)
        val search = findViewById<ImageButton>(R.id.search)
        search.load(R.drawable.logo_search)
        val login = findViewById<ImageButton>(R.id.login)
        login.load(R.drawable.login)
        val my = findViewById<ImageButton>(R.id.my)
        my.load(R.drawable.mypage)


        trend.setOnClickListener{
            val trendIntent = Intent(this, TrendActivity::class.java)
            startActivity(trendIntent)
        }
        search.setOnClickListener{
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        login.setOnClickListener {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/authentication/token/new?api_key=$apiKey")
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val responseBody = response.body!!.string()
                        val jsonArray = JSONObject(responseBody)

                        requestToken = jsonArray.getString("request_token")
                        println("Request Token: $requestToken")

                        val redirectUrl = "movie-app://approved"

                        val authenticationUrl =
                            "https://www.themoviedb.org/authenticate/$requestToken?redirect_to=$redirectUrl"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authenticationUrl))
                        intent.putExtra("requestToken", requestToken)
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }
        my.setOnClickListener {
            if(requestToken.isNullOrEmpty()){
                Toast.makeText(this@MainActivity, "Login First!", Toast.LENGTH_SHORT).show()
            }
            else{
                val client = OkHttpClient()
                println(requestToken)

                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = JSONObject().apply {
                    put("request_token", requestToken)
                }.toString()
                val body = requestBody.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("https://api.themoviedb.org/3/authentication/session/new?api_key=$apiKey&request_token=$requestToken")
                    .post(body)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            val responseBody = response.body!!.string()

                            val jsonObject = JSONObject(responseBody)
                            val sessionID = jsonObject.getString("session_id")
                            println("sessionID: $sessionID")

                            GlobalVariables.session = sessionID

                            println("Global session: ${GlobalVariables.session}")


                            CoroutineScope(Dispatchers.Main).launch {
                                val myIntent = Intent(this@MainActivity, MyActivity::class.java).apply{
                                    putExtra(EXT_ID, sessionID)
                                }
                                startActivity(myIntent)
                            }
                        }
                    }
                })
            }
        }

    }
    override fun onResume() {
        super.onResume()
    }

}

