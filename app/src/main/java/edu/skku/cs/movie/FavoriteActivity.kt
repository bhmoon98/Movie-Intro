package edu.skku.cs.movie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class FavoriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        String AUTH_TOKEN = BuildConfig.auth_token;

        val sessionID = intent.getStringExtra(MyActivity.EXT_ID)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/account/19746926/favorite/movies?language=en-US&page=1&sort_by=created_at.asc")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
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
                    val resultsArray = jsonArray.getJSONArray("results")

                    CoroutineScope(Dispatchers.Main).launch {
                        val favoriteList = mutableListOf<Movie>()
                        for (i in 0 until resultsArray.length()) {
                            val resultObject = resultsArray.getJSONObject(i)
                            val id = resultObject.getInt("id")
                            val title = resultObject.getString("title")
                            val backdropPath = resultObject.getString("backdrop_path")
                            val posterPath = resultObject.getString("poster_path")
                            val voteAverage = resultObject.getDouble("vote_average")
                            val releaseDate = resultObject.getString("release_date")
                            val genreArray = resultObject.getJSONArray("genre_ids")
                            val genreList = mutableListOf<Int>()
                            val overview = resultObject.getString("overview")

                            for (i in 0 until genreArray.length()) {
                                val genreId = genreArray.getInt(i)
                                genreList.add(genreId)
                            }
                            val movie = Movie(id, title, backdropPath, posterPath, voteAverage, releaseDate, genreList, overview, true, 0.0)
                            favoriteList.add(movie)
                        }
                        for (i in 0 until favoriteList.size){
                            println(favoriteList.get(i).id)
                            println(favoriteList.get(i).title)
                            println(favoriteList.get(i).backdropPath)
                            println(favoriteList.get(i).posterPath)
                            println(favoriteList.get(i).voteAverage)
                            println(favoriteList.get(i).releaseDate)
                            println(favoriteList.get(i).genreList)
                        }

                        val adapter = FavoriteListAdapter(favoriteList, this@FavoriteActivity)
                        var listView = findViewById<ListView>(R.id.favoriteListView)
                        listView.adapter = adapter

                        adapter.notifyDataSetChanged()


                    }
                }
            }
        })
    }
}