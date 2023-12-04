package edu.skku.cs.movie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        
        String AUTH_TOKEN = BuildConfig.auth_token;
        val sessionID = intent.getStringExtra(MyActivity.EXT_ID)

        val btn = findViewById<Button>(R.id.button)
        val et = findViewById<EditText>(R.id.editText)

        val client = OkHttpClient()

        btn.setOnClickListener{
            val query = et.text.toString()

            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/search/movie?query=$query&include_adult=false&language=en-US&page=1&sort_by=popularity.desc")
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
                        println(resultsArray.length())

                        CoroutineScope(Dispatchers.Main).launch {
                            // 필요한 정보만 저장하기
                            val searchList = mutableListOf<Movie>()
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
                                val movie = Movie(id, title, backdropPath, posterPath, voteAverage, releaseDate, genreList, overview, false, 0.0)
                                searchList.add(movie)
                            }

                            for (i in 0 until searchList.size){
                                println(searchList.get(i).id)
                                println(searchList.get(i).title)
                                println(searchList.get(i).backdropPath)
                                println(searchList.get(i).posterPath)
                                println(searchList.get(i).voteAverage)
                                println(searchList.get(i).releaseDate)
                                println(searchList.get(i).genreList)
                            }

                            val adapter = SearchListAdapter(searchList, this@SearchActivity)
                            var listView = findViewById<ListView>(R.id.listView)
                            listView.adapter = adapter

                            adapter.notifyDataSetChanged()

                        }
                    }
                }
            })
        }
    }
}