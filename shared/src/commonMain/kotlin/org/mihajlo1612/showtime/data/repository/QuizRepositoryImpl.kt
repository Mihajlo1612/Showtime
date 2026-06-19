package org.mihajlo1612.showtime.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mihajlo1612.showtime.data.local.db.dao.CastDao
import org.mihajlo1612.showtime.data.local.db.dao.MovieDao
import org.mihajlo1612.showtime.data.local.db.dao.QuizSessionDao
import org.mihajlo1612.showtime.data.mapper.toDomain
import org.mihajlo1612.showtime.data.mapper.toEntity
import org.mihajlo1612.showtime.data.remote.api.QuizApi
import org.mihajlo1612.showtime.domain.model.Movie
import org.mihajlo1612.showtime.domain.model.QuizQuestion
import org.mihajlo1612.showtime.domain.model.QuizSession
import org.mihajlo1612.showtime.domain.model.QuizType
import org.mihajlo1612.showtime.domain.repository.MovieRepository
import org.mihajlo1612.showtime.domain.repository.QuizRepository

private const val SESSION_SIZE = 10
private const val MAX_PER_TYPE = 4
private const val MIN_POOL = 10
private const val TARGET_POOL = 100
private const val IMG_BASE = "https://image.tmdb.org/t/p/w780"

class QuizRepositoryImpl(
    private val movieDao: MovieDao,
    private val castDao: CastDao,
    private val quizSessionDao: QuizSessionDao,
    private val quizApi: QuizApi,
    private val movieRepository: MovieRepository,
) : QuizRepository {

    override suspend fun ensureQuizPool() {
        if (movieDao.countWithImage() >= MIN_POOL) return
        var page = 1
        while (movieDao.countWithImage() < TARGET_POOL && page <= 5) {
            try {
                movieRepository.syncMovies(page)
            } catch (e: Exception) {
                break
            }
            page++
        }
    }

    override suspend fun hasEnoughForQuiz(): Boolean =
        movieDao.countWithImage() >= MIN_POOL

    override suspend fun generateQuiz(): List<QuizQuestion> {
        val movies = movieDao.getAllOnce().map { it.toDomain() }
            .filter { it.posterPath != null || it.backdropPath != null }
        if (movies.size < MIN_POOL) return emptyList()

        val idsWithCast = castDao.getMovieIdsWithCast().toSet()
        val allActorNames = castDao.getAllActorNames()

        val questions = mutableListOf<QuizQuestion>()
        val usedMovieIds = mutableSetOf<String>()
        val typeCounts = mutableMapOf<QuizType, Int>()

        var guard = 0
        val maxGuard = movies.size * 5

        while (questions.size < SESSION_SIZE && guard < maxGuard) {
            guard++
            val movie = movies.random()
            if (movie.imdbId in usedMovieIds) continue

            // NOVO:
            val supported = QuizType.entries.filter { movieSupports(movie, it, idsWithCast) }
            if (supported.isEmpty()) continue

            val underCap = supported.filter { (typeCounts[it] ?: 0) < MAX_PER_TYPE }
            val type = (if (underCap.isNotEmpty()) underCap else supported).random()
            val q = when (type) {
                QuizType.GUESS_MOVIE -> buildGuessMovie(movie, movies)
                QuizType.GUESS_YEAR -> buildGuessYear(movie)
                QuizType.GUESS_ACTOR -> buildGuessActor(movie, allActorNames)
            } ?: continue

            questions += q
            usedMovieIds += movie.imdbId
            typeCounts[type] = (typeCounts[type] ?: 0) + 1
        }

        return questions
    }

    private fun movieSupports(movie: Movie, type: QuizType, idsWithCast: Set<String>): Boolean =
        when (type) {
            QuizType.GUESS_MOVIE -> movie.posterPath != null || movie.backdropPath != null
            QuizType.GUESS_YEAR -> movie.year != null && movie.posterPath != null
            QuizType.GUESS_ACTOR -> movie.posterPath != null && movie.imdbId in idsWithCast
        }

    private fun buildGuessMovie(movie: Movie, all: List<Movie>): QuizQuestion? {
        val path = movie.backdropPath ?: movie.posterPath ?: return null
        val wrong = all.asSequence()
            .filter { it.imdbId != movie.imdbId }
            .map { it.title }
            .distinct()
            .filter { it != movie.title }
            .toList()
            .shuffled()
            .take(3)
        if (wrong.size < 3) return null
        return QuizQuestion(
            type = QuizType.GUESS_MOVIE,
            movieImdbId = movie.imdbId,
            imageUrl = "$IMG_BASE$path",
            title = null,
            prompt = "Guess the movie from the image",
            options = (wrong + movie.title).shuffled(),
            correctAnswer = movie.title,
        )
    }

    private fun buildGuessYear(movie: Movie): QuizQuestion? {
        val year = movie.year ?: return null
        val poster = movie.posterPath ?: return null
        val offsets = listOf(-10, -7, -5, -3, -2, -1, 1, 2, 3, 5, 7, 10).shuffled()
        val wrong = linkedSetOf<Int>()
        for (o in offsets) {
            if (wrong.size == 3) break
            val y = year + o
            if (y != year) wrong += y
        }
        if (wrong.size < 3) return null
        val options = (wrong.map { it.toString() } + year.toString()).shuffled()
        return QuizQuestion(
            type = QuizType.GUESS_YEAR,
            movieImdbId = movie.imdbId,
            imageUrl = "$IMG_BASE$poster",
            title = movie.title,
            prompt = "In which year was this movie released?",
            options = options,
            correctAnswer = year.toString(),
        )
    }

    private suspend fun buildGuessActor(movie: Movie, allActorNames: List<String>): QuizQuestion? {
        val poster = movie.posterPath ?: return null
        val cast = castDao.getCastOnce(movie.imdbId)
        val actors = cast.filter { it.department == "Acting" }.ifEmpty { cast }
        if (actors.isEmpty()) return null
        val correct = actors.take(3).random().name
        val inThisMovie = cast.map { it.name }.toSet()
        val wrong = allActorNames
            .filter { it !in inThisMovie }
            .distinct()
            .shuffled()
            .take(3)
        if (wrong.size < 3) return null
        return QuizQuestion(
            type = QuizType.GUESS_ACTOR,
            movieImdbId = movie.imdbId,
            imageUrl = "$IMG_BASE$poster",
            title = movie.title,
            prompt = "Who stars in this movie?",
            options = (wrong + correct).shuffled(),
            correctAnswer = correct,
        )
    }

    override suspend fun submitResult(score: Float): Int =
        try {
            quizApi.submitResult(score, category = 1).ranking
        } catch (e: Exception) {
            -1
        }

    override suspend fun saveSession(session: QuizSession) {
        quizSessionDao.insert(session.toEntity())
    }

    override fun observeSessions(): Flow<List<QuizSession>> =
        quizSessionDao.observeAll().map { list -> list.map { it.toDomain() } }
}