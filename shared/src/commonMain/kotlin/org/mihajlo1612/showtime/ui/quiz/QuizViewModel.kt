package org.mihajlo1612.showtime.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mihajlo1612.showtime.domain.QuizScoring
import org.mihajlo1612.showtime.domain.model.QuizQuestion
import org.mihajlo1612.showtime.domain.model.QuizSession
import org.mihajlo1612.showtime.domain.repository.QuizRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

enum class QuizPhase { Loading, NotEnough, Playing, Finished }

data class QuizUiState(
    val phase: QuizPhase = QuizPhase.Loading,
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val correctCount: Int = 0,
    val remainingSeconds: Int = QuizScoring.MAX_TIME_SECONDS,
    val finalScore: Float = 0f,
    val ranking: Int? = null,
    val submitError: String? = null,
) {
    val current: QuizQuestion? get() = questions.getOrNull(currentIndex)
    val answered: Boolean get() = selectedAnswer != null
    val totalQuestions: Int get() = questions.size
    val wrongCount: Int get() = totalQuestions - correctCount
    val timeUsed: Int get() = QuizScoring.MAX_TIME_SECONDS - remainingSeconds
}

sealed class QuizUiEvent {
    data class AnswerSelected(val answer: String) : QuizUiEvent()
    data object Restart : QuizUiEvent()
    data object Abandon : QuizUiEvent()
}

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var advanceJob: Job? = null

    init { start() }

    fun onEvent(event: QuizUiEvent) {
        when (event) {
            is QuizUiEvent.AnswerSelected -> selectAnswer(event.answer)
            is QuizUiEvent.Restart -> start()
            is QuizUiEvent.Abandon -> abandon()
        }
    }

    private fun start() {
        timerJob?.cancel()
        advanceJob?.cancel()
        _uiState.value = QuizUiState(phase = QuizPhase.Loading)
        viewModelScope.launch {
            quizRepository.ensureQuizPool()
            if (!quizRepository.hasEnoughForQuiz()) {
                _uiState.update { it.copy(phase = QuizPhase.NotEnough) }
                return@launch
            }
            val questions = quizRepository.generateQuiz()
            if (questions.size < SESSION_MIN) {
                _uiState.update { it.copy(phase = QuizPhase.NotEnough) }
                return@launch
            }
            _uiState.update {
                it.copy(
                    phase = QuizPhase.Playing,
                    questions = questions,
                    currentIndex = 0,
                    selectedAnswer = null,
                    correctCount = 0,
                    remainingSeconds = QuizScoring.MAX_TIME_SECONDS,
                )
            }
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000.milliseconds)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            finish()
        }
    }

    private fun selectAnswer(answer: String) {
        val s = _uiState.value
        if (s.phase != QuizPhase.Playing || s.answered) return
        val correct = s.current?.correctAnswer
        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                correctCount = if (answer == correct) it.correctCount + 1 else it.correctCount,
            )
        }
        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            delay(2000.milliseconds)
            advance()
        }
    }

    private fun advance() {
        val s = _uiState.value
        if (s.phase != QuizPhase.Playing) return
        if (s.currentIndex >= s.questions.lastIndex) {
            finish()
        } else {
            _uiState.update { it.copy(currentIndex = it.currentIndex + 1, selectedAnswer = null) }
        }
    }

    private fun finish() {
        val s = _uiState.value
        if (s.phase == QuizPhase.Finished) return
        timerJob?.cancel()
        advanceJob?.cancel()
        val score = QuizScoring.score(s.correctCount, s.remainingSeconds)
        _uiState.update { it.copy(phase = QuizPhase.Finished, finalScore = score) }

        viewModelScope.launch {
            try {
                quizRepository.saveSession(
                    QuizSession(
                        score = score,
                        correctAnswers = s.correctCount,
                        totalQuestions = s.totalQuestions,
                        timeUsedSeconds = s.timeUsed,
                        playedAt = Clock.System.now().toEpochMilliseconds(),
                    )
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(submitError = "saveSession: ${e.message}") }
            }

            try {
                val ranking = quizRepository.submitResult(score)
                _uiState.update { it.copy(ranking = ranking) }
            } catch (e: Exception) {
                _uiState.update { it.copy(submitError = "submit: ${e::class.simpleName}: ${e.message}") }
            }
        }
    }

    private fun abandon() {
        timerJob?.cancel()
        advanceJob?.cancel()
        start()
    }

    override fun onCleared() {
        timerJob?.cancel()
        advanceJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val SESSION_MIN = 10
    }
}