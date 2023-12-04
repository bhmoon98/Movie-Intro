package edu.skku.cs.movie

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ListView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MovieActivity : AppCompatActivity() {
    private val sessionID = GlobalVariables.session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        String AUTH_TOKEN = BuildConfig.auth_token;

        val id = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title")
        val backdropPath = intent.getStringExtra("backdropPath")
        val posterPath = intent.getStringExtra("posterPath")
        val voteAverage = intent.getDoubleExtra("voteAverage", 0.0)
        val releaseDate = intent.getStringExtra("releaseDate")
        val overview = intent.getStringExtra("overview")
        val genreList = intent.getIntegerArrayListExtra("genreList")

        val titleView = findViewById<TextView>(R.id.movieTitle)
        val backdropView = findViewById<ImageView>(R.id.backdropImageView)
        val posterView = findViewById<ImageView>(R.id.posterImageView)
        val date = findViewById<TextView>(R.id.movieReleaseDate2)
        val actors = findViewById<TextView>(R.id.actors2)
        val genres = findViewById<TextView>(R.id.genre2)
        val rating = findViewById<TextView>(R.id.voteAverage2)
        val overviewText = findViewById<TextView>(R.id.overview)
        val checkBox = findViewById<CheckBox>(R.id.favoriteBox)
        val myRating = findViewById<RatingBar>(R.id.ratingBar)
        var isRated = false


        println(sessionID)

        titleView.text = title
        rating.text = voteAverage.toString()
        date.text = releaseDate
        if (overview?.length!! > 200) {
            val truncatedOverview = overview?.substring(0, 200) + "..."
            overviewText.text = truncatedOverview
        } else {
            overviewText.text = overview
        }

        if (posterPath?.endsWith(".jpg") == true) {
            val imageUrl = "https://image.tmdb.org/t/p/w500${posterPath}"
            posterView.load(imageUrl)
        }
        else {
            posterView.setImageResource(R.drawable.noposter)
        }
        if (backdropPath?.endsWith(".jpg") == true) {
            val imageUrl = "https://image.tmdb.org/t/p/w500${backdropPath}"
            backdropView.load(imageUrl)
        }
        else {
            backdropView.setImageResource(R.drawable.noposter)
        }

        val client = OkHttpClient()

        val requestActor = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/$id/credits?language=en-US")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
            .build()

        client.newCall(requestActor).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body!!.string()
                    val jsonArray = JSONObject(responseBody)
                    val resultsArray = jsonArray.getJSONArray("cast")

                    CoroutineScope(Dispatchers.Main).launch {
                        val castData = mutableListOf<Cast>()
                        for (i in 0 until resultsArray.length()) {
                            val resultObject = resultsArray.getJSONObject(i)
                            val dept = resultObject.getString("known_for_department")
                            val name = resultObject.getString("name")
                            val cast = Cast(dept, name)
                            castData.add(cast)
                        }

                        val movieCasts = mutableListOf<String>()
                        val maxDisplayCount = 3 // 최대 표시할 배우 수

                        for ((index, people) in castData.withIndex()) {
                            if (people.dept == "Acting") {
                                movieCasts.add(people.name)
                            }

                            if (index == maxDisplayCount - 1) {
                                break
                            }
                        }

                        val actorsText = movieCasts.joinToString(", ")
                        actors.text = actorsText

                    }
                }
            }
        })

        
        //genre list 불러와서 실제 genre 출력하기
        val requestGenre = Request.Builder()
            .url("https://api.themoviedb.org/3/genre/movie/list?language=en")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
            .build()

        client.newCall(requestGenre).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body!!.string()
                    val jsonArray = JSONObject(responseBody)
                    val resultsArray = jsonArray.getJSONArray("genres")

                    CoroutineScope(Dispatchers.Main).launch {
                        val genreData = mutableListOf<Genre>()
                        for (i in 0 until resultsArray.length()) {
                            val resultObject = resultsArray.getJSONObject(i)
                            val genreID = resultObject.getInt("id")
                            val name = resultObject.getString("name")
                            val genre = Genre(genreID, name)
                            genreData.add(genre)
                        }

                        val movieGenres = mutableListOf<String>()
                        if (!genreList.isNullOrEmpty()) {
                            for (genreId in genreList) {
                                for (genre in genreData) {
                                    if (genre.id == genreId) {
                                        movieGenres.add(genre.name)
                                        break
                                    }
                                }
                            }
                        }

                        val genresText = movieGenres.joinToString(", ")
                        genres.text = genresText

                    }
                }
            }
        })

        val requestFavorite = Request.Builder()
            .url("https://api.themoviedb.org/3/account/19746926/favorite/movies?language=en-US&page=1&sort_by=created_at.asc")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
            .build()
        client.newCall(requestFavorite).enqueue(object : Callback {
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
                        val idList = mutableListOf<Int>()
                        for (i in 0 until resultsArray.length()) {
                            val resultObject = resultsArray.getJSONObject(i)
                            val idCheck = resultObject.getInt("id")
                            idList.add(idCheck)
                        }
                        if (id in idList) {
                            checkBox.isChecked = true
                        }
                    }
                }
            }
        })

        //Rating 정보 가져오기
        val requestRating = Request.Builder()
            .url("https://api.themoviedb.org/3/account/19746926/rated/movies?language=en-US&page=1&sort_by=created_at.asc")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
            .build()
        client.newCall(requestRating).enqueue(object : Callback {
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
                        val idList = mutableListOf<Int>()
                        val ratingList = mutableListOf<Double>()
                        for (i in 0 until resultsArray.length()) {
                            val resultObject = resultsArray.getJSONObject(i)
                            val idCheck = resultObject.getInt("id")
                            val myRatingScore = resultObject.getDouble("rating")
                            idList.add(idCheck)
                            ratingList.add(myRatingScore)
                        }
                        if (id in idList) {
                            if (id in idList) {
                                val index = idList.indexOf(id)
                                val rating = ratingList[index].toFloat() // 데이터 값 (예시)
                                val maxRating = 10f // 최대 점수 (예시)
                                val scale = 5f / maxRating // 5점 기준에 맞춰서 스케일링
                                val scaledRating = rating * scale // 데이터 값 스케일링

                                myRating.rating = scaledRating
                                println(myRating.rating)
                                isRated = true
                            }
                        }
                    }
                }
            }
        })

        if (sessionID != "") {
            if (!checkBox.isChecked){
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        sendFavoriteRequest(id)
                    }
                }
            }
            else{
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        checkBox.isChecked = true
                        Toast.makeText(this@MovieActivity, "Doesn't support delete!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            myRating.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    val actualRating = rating * 2
                    val rateInt = actualRating.toInt()
                    val ratingJson = "{\"value\": $rateInt}"

                    Thread {
                        try {
                            object : AsyncTask<Unit, Unit, Response>() {
                                override fun doInBackground(vararg params: Unit?): Response {
                                    val mediaType =
                                        "application/json;charset=utf-8".toMediaTypeOrNull()
                                    val body = RequestBody.create(mediaType, ratingJson)
                                    val requestRating = Request.Builder()
                                        .url("https://api.themoviedb.org/3/movie/$id/rating?session_id=$sessionID")
                                        .post(body)
                                        .addHeader("accept", "application/json")
                                        .addHeader("Content-Type", "application/json;charset=utf-8")
                                        .addHeader(
                                            "Authorization",
                                            AUTH_TOKEN
                                        )
                                        .build()
                                    val response = client.newCall(requestRating).execute()
                                    return response
                                }

                                override fun onPostExecute(result: Response) {
                                    if (result.isSuccessful) {
                                        // Process the successful response
                                        val responseBody = result.body?.string()
                                        println(responseBody)
                                        // Perform additional processing
                                    } else {
                                        println("Access Failed")
                                    }
                                }
                            }.execute()
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                }
            }


        }

    }
    fun sendFavoriteRequest(id:Int) {
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()
        val body =
            "{\"media_type\":\"movie\",\"media_id\":$id,\"favorite\":true}".toRequestBody(mediaType)
        val requestFavorite = Request.Builder()
            .url("https://api.themoviedb.org/3/account/19746926/favorite?session_id=$sessionID")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("content-type", "application/json")
            .addHeader("Authorization", AUTH_TOKEN)
            .build()
        client.newCall(requestFavorite).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    // Handle response
                }
            }
        })
    }
}