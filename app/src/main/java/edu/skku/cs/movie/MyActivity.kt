package edu.skku.cs.movie

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

var session : String?= null

class MyActivity : AppCompatActivity() {
    String apiKey = BuildConfig.api_key;
    String AUTH_TOKEN = BuildConfig.auth_token;
    companion object{
        const val EXT_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)
        val sessionID = intent.getStringExtra(MainActivity.EXT_ID)
        session = sessionID
        println(session)

        val client = OkHttpClient()

        //username 획득
        val requestAccount = Request.Builder()
            .url("https://api.themoviedb.org/3/account/19746926?session_id=$sessionID")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
            .build()

        client.newCall(requestAccount).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body!!.string()

                    val jsonObject = JSONObject(responseBody)
                    val username = jsonObject.getString("username")
                    println("username: $username")

                    CoroutineScope(Dispatchers.Main).launch {
                        val userID = findViewById<TextView>(R.id.userName)
                        userID.text = "Hello, ${username}"
                    }
                }
            }
        })

        val addFavorite = findViewById<Button>(R.id.addFavorite)
        val addRating = findViewById<Button>(R.id.addRating)
        val myFavorite = findViewById<Button>(R.id.listFavorite)
        val myRating = findViewById<Button>(R.id.listRating)
        addFavorite.setOnClickListener{
            val searchIntent = Intent(this, SearchActivity::class.java).apply{
                putExtra(EXT_ID, sessionID)
            }
            startActivity(searchIntent)
        }
        addRating.setOnClickListener{
            val searchIntent = Intent(this, SearchActivity::class.java).apply{
                putExtra(EXT_ID, sessionID)
            }
            startActivity(searchIntent)
        }
        myFavorite.setOnClickListener{
            val favoriteIntent = Intent(this, FavoriteActivity::class.java).apply{
                putExtra(EXT_ID, sessionID)
            }
            startActivity(favoriteIntent)
        }
        myRating.setOnClickListener{
            val ratingIntent = Intent(this, RatingActivity::class.java).apply{
                putExtra(EXT_ID, sessionID)
            }
            startActivity(ratingIntent)
        }


    }
}