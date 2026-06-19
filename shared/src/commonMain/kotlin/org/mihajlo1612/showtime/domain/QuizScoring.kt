package org.mihajlo1612.showtime.domain

object QuizScoring {
    const val MAX_TIME_SECONDS = 60

    /** UBP = BTO * (9 + PVT/MVT), ograniceno na 100.00 */
    fun score(correctAnswers: Int, remainingSeconds: Int): Float {
        val raw = correctAnswers * (9f + remainingSeconds.toFloat() / MAX_TIME_SECONDS)
        return minOf(raw, 100f)
    }
}