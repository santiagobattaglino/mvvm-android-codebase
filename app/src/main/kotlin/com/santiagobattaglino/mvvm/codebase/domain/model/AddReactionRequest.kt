package com.santiagobattaglino.mvvm.codebase.domain.model

class AddReactionRequest(
    val reaction: String = "",
    val incidentId: Int = 0
) {
    companion object {
        const val FEARFUL_FACE = "FEARFUL_FACE"
        const val HANDS_PRESSED_TOGETHER = "HANDS_PRESSED_TOGETHER"
        const val RED_ANGRY_FACE = "RED_ANGRY_FACE"
        const val RED_HEART = "RED_HEART"
    }
}