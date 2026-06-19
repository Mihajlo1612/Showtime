package org.mihajlo1612.showtime.domain.model

enum class QuizType { GUESS_MOVIE, GUESS_YEAR, GUESS_ACTOR }

data class QuizQuestion(
    val type: QuizType,
    val movieImdbId: String,
    val imageUrl: String,        // pun URL slike za prikaz
    val title: String?,          // naslov ispod slike; null za GUESS_MOVIE
    val prompt: String,          // tekst pitanja
    val options: List<String>,   // 4 opcije, izmešane
    val correctAnswer: String,
)