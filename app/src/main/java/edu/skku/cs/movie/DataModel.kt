package edu.skku.cs.movie

data class Movie(
    val id: Int,
    val title: String,
    val backdropPath: String?,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val genreList: MutableList<Int>?,
    val overview: String,
    val favorite: Boolean,
    val rating: Double
)

data class Genre(
    val id: Int,
    val name: String
)

data class Cast(
    val dept: String,
    val name: String
)