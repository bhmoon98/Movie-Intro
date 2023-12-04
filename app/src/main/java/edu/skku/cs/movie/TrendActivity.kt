package edu.skku.cs.movie

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.*
import java.io.IOException

class TrendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trend)

        String AUTH_TOKEN = BuildConfig.auth_token;

        val nowShowingImageView = findViewById<ImageView>(R.id.nowShowing)
        nowShowingImageView.load(R.drawable.nowshowing)
        val topRatedImageView = findViewById<ImageView>(R.id.topRated)
        topRatedImageView.load(R.drawable.toprated)

        val client = OkHttpClient()

        val requestNowPlaying = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
            .build()

        client.newCall(requestNowPlaying).enqueue(object : Callback {
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
                        val nowPlayingList = mutableListOf<Movie>()
                        val itemCount = minOf(resultsArray.length(), 5)
                        for (i in 0 until itemCount) {
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
                            nowPlayingList.add(movie)
                        }

                        for (i in 0 until nowPlayingList.size){
                            println(nowPlayingList.get(i).id)
                            println(nowPlayingList.get(i).title)
                            println(nowPlayingList.get(i).backdropPath)
                            println(nowPlayingList.get(i).posterPath)
                            println(nowPlayingList.get(i).voteAverage)
                            println(nowPlayingList.get(i).releaseDate)
                            println(nowPlayingList.get(i).genreList)
                        }

                        val imgBtn = findViewById<ImageButton>(R.id.imageButton)
                        val imgBtn2 = findViewById<ImageButton>(R.id.imageButton2)
                        val imgBtn3 = findViewById<ImageButton>(R.id.imageButton3)
                        val imgBtn4 = findViewById<ImageButton>(R.id.imageButton4)
                        val imgBtn5 = findViewById<ImageButton>(R.id.imageButton5)

                        for (i in 0 until minOf(nowPlayingList.size, 5)) {
                            val movie = nowPlayingList[i]
                            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

                            when (i) {
                                0 -> imgBtn.load(imageUrl)
                                1 -> imgBtn2.load(imageUrl)
                                2 -> imgBtn3.load(imageUrl)
                                3 -> imgBtn4.load(imageUrl)
                                4 -> imgBtn5.load(imageUrl)
                            }
                        }

                        imgBtn.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", nowPlayingList[0].id)
                            intent.putExtra("title", nowPlayingList[0].title)
                            intent.putExtra("backdropPath", nowPlayingList[0].backdropPath)
                            intent.putExtra("posterPath", nowPlayingList[0].posterPath)
                            intent.putExtra("voteAverage", nowPlayingList[0].voteAverage)
                            intent.putExtra("releaseDate", nowPlayingList[0].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(nowPlayingList[0].genreList))
                            intent.putExtra("overview", nowPlayingList[0].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        imgBtn2.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", nowPlayingList[1].id)
                            intent.putExtra("title", nowPlayingList[1].title)
                            intent.putExtra("backdropPath", nowPlayingList[1].backdropPath)
                            intent.putExtra("posterPath", nowPlayingList[1].posterPath)
                            intent.putExtra("voteAverage", nowPlayingList[1].voteAverage)
                            intent.putExtra("releaseDate", nowPlayingList[1].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(nowPlayingList[1].genreList))
                            intent.putExtra("overview", nowPlayingList[1].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        imgBtn3.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", nowPlayingList[2].id)
                            intent.putExtra("title", nowPlayingList[2].title)
                            intent.putExtra("backdropPath", nowPlayingList[2].backdropPath)
                            intent.putExtra("posterPath", nowPlayingList[2].posterPath)
                            intent.putExtra("voteAverage", nowPlayingList[2].voteAverage)
                            intent.putExtra("releaseDate", nowPlayingList[2].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(nowPlayingList[2].genreList))
                            intent.putExtra("overview", nowPlayingList[2].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        imgBtn4.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", nowPlayingList[3].id)
                            intent.putExtra("title", nowPlayingList[3].title)
                            intent.putExtra("backdropPath", nowPlayingList[3].backdropPath)
                            intent.putExtra("posterPath", nowPlayingList[3].posterPath)
                            intent.putExtra("voteAverage", nowPlayingList[3].voteAverage)
                            intent.putExtra("releaseDate", nowPlayingList[3].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(nowPlayingList[3].genreList))
                            intent.putExtra("overview", nowPlayingList[3].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        imgBtn5.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", nowPlayingList[4].id)
                            intent.putExtra("title", nowPlayingList[4].title)
                            intent.putExtra("backdropPath", nowPlayingList[4].backdropPath)
                            intent.putExtra("posterPath", nowPlayingList[4].posterPath)
                            intent.putExtra("voteAverage", nowPlayingList[4].voteAverage)
                            intent.putExtra("releaseDate", nowPlayingList[4].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(nowPlayingList[4].genreList))
                            intent.putExtra("overview", nowPlayingList[4].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                    }
                }
            }
        })

        val requestTopRated = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
            .build()

        client.newCall(requestTopRated).enqueue(object : Callback {
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
                        val topRatedList = mutableListOf<Movie>()
                        val itemCount = minOf(resultsArray.length(), 5)
                        for (i in 0 until itemCount) {
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
                            topRatedList.add(movie)

                        }

                        val imgBtn6 = findViewById<ImageButton>(R.id.imageButton6)
                        val imgBtn7 = findViewById<ImageButton>(R.id.imageButton7)
                        val imgBtn8 = findViewById<ImageButton>(R.id.imageButton8)
                        val imgBtn9 = findViewById<ImageButton>(R.id.imageButton9)
                        val imgBtn10 = findViewById<ImageButton>(R.id.imageButton10)

                        for (i in 0 until minOf(topRatedList.size, 5)) {
                            val movie = topRatedList[i]
                            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
                            when (i) {
                                0 -> imgBtn6.load(imageUrl)
                                1 -> imgBtn7.load(imageUrl)
                                2 -> imgBtn8.load(imageUrl)
                                3 -> imgBtn9.load(imageUrl)
                                4 -> imgBtn10.load(imageUrl)
                            }
                        }

                        imgBtn6.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", topRatedList[0].id)
                            intent.putExtra("title", topRatedList[0].title)
                            intent.putExtra("backdropPath", topRatedList[0].backdropPath)
                            intent.putExtra("posterPath", topRatedList[0].posterPath)
                            intent.putExtra("voteAverage", topRatedList[0].voteAverage)
                            intent.putExtra("releaseDate", topRatedList[0].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(topRatedList[0].genreList))
                            intent.putExtra("overview", topRatedList[0].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        imgBtn7.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", topRatedList[1].id)
                            intent.putExtra("title", topRatedList[1].title)
                            intent.putExtra("backdropPath", topRatedList[1].backdropPath)
                            intent.putExtra("posterPath", topRatedList[1].posterPath)
                            intent.putExtra("voteAverage", topRatedList[1].voteAverage)
                            intent.putExtra("releaseDate", topRatedList[1].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(topRatedList[1].genreList))
                            intent.putExtra("overview", topRatedList[1].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        imgBtn8.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", topRatedList[2].id)
                            intent.putExtra("title", topRatedList[2].title)
                            intent.putExtra("backdropPath", topRatedList[2].backdropPath)
                            intent.putExtra("posterPath", topRatedList[2].posterPath)
                            intent.putExtra("voteAverage", topRatedList[2].voteAverage)
                            intent.putExtra("releaseDate", topRatedList[2].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(topRatedList[2].genreList))
                            intent.putExtra("overview", topRatedList[2].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        imgBtn9.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", topRatedList[3].id)
                            intent.putExtra("title", topRatedList[3].title)
                            intent.putExtra("backdropPath", topRatedList[3].backdropPath)
                            intent.putExtra("posterPath", topRatedList[3].posterPath)
                            intent.putExtra("voteAverage", topRatedList[3].voteAverage)
                            intent.putExtra("releaseDate", topRatedList[3].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(topRatedList[3].genreList))
                            intent.putExtra("overview", topRatedList[3].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        imgBtn10.setOnClickListener{
                            val intent = Intent(this@TrendActivity, MovieActivity::class.java)
                            intent.putExtra("id", topRatedList[4].id)
                            intent.putExtra("title", topRatedList[4].title)
                            intent.putExtra("backdropPath", topRatedList[4].backdropPath)
                            intent.putExtra("posterPath", topRatedList[4].posterPath)
                            intent.putExtra("voteAverage", topRatedList[4].voteAverage)
                            intent.putExtra("releaseDate", topRatedList[4].releaseDate)
                            intent.putIntegerArrayListExtra("genreList", ArrayList(topRatedList[4].genreList))
                            intent.putExtra("overview", topRatedList[4].overview)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                    }
                }
            }
        })



    }
}