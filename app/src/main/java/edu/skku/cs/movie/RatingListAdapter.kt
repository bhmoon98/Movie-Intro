package edu.skku.cs.movie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import coil.load

class RatingListAdapter(private val data: MutableList<Movie>, private val context: Context): BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view: View = convertView ?: inflater.inflate(R.layout.item_search, parent, false)

        val searchImage = view.findViewById<ImageButton>(R.id.searchImageView)
        val title = view.findViewById<TextView>(R.id.searchTitle)
        val rating = view.findViewById<TextView>(R.id.showRating)

        title.text = data[position].title
        rating.text = data[position].rating.toString()
        val movie = data[position]
        if (movie.posterPath?.endsWith(".jpg") == true) {
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            searchImage.load(imageUrl)
            println(movie.posterPath)
        } else if (movie.backdropPath?.endsWith(".jpg") == true) {
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.backdropPath}"
            searchImage.load(imageUrl)
        } else {
            searchImage.setImageResource(R.drawable.noposter)
        }

        title.setOnClickListener{
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra("id", data[position].id)
            intent.putExtra("title", data[position].title)
            intent.putExtra("backdropPath", data[position].backdropPath)
            intent.putExtra("posterPath", data[position].posterPath)
            intent.putExtra("voteAverage", data[position].voteAverage)
            intent.putExtra("releaseDate", data[position].releaseDate)
            intent.putIntegerArrayListExtra("genreList", ArrayList(data[position].genreList))
            intent.putExtra("overview", data[position].overview)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
        searchImage.setOnClickListener{
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra("id", data[position].id)
            intent.putExtra("title", data[position].title)
            intent.putExtra("backdropPath", data[position].backdropPath)
            intent.putExtra("posterPath", data[position].posterPath)
            intent.putExtra("voteAverage", data[position].voteAverage)
            intent.putExtra("releaseDate", data[position].releaseDate)
            intent.putIntegerArrayListExtra("genreList", ArrayList(data[position].genreList))
            intent.putExtra("overview", data[position].overview)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        return view
    }

}