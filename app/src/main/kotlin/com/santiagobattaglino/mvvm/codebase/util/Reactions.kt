package com.santiagobattaglino.mvvm.codebase.util

import com.santiagobattaglino.mvvm.codebase.domain.entity.Incident
import android.content.Context
import android.view.View
import com.santiagobattaglino.mvvm.codebase.R
import kotlinx.android.synthetic.main.popup_reactions.view.*

fun getReactionsCount(incident: Incident): Int {
    return incident.redAngryFaceReactions + incident.redHeartReactions + incident.fearfulFaceReactions + incident.handsPressedReactions
}

fun getMostSelectedReaction(context: Context, incident: Incident): String {
    val reactions = listOf(
        incident.fearfulFaceReactions,
        incident.handsPressedReactions,
        incident.redAngryFaceReactions,
        incident.redHeartReactions
    )
    val mostSelected = reactions.maxOrNull() ?: 0
    return when (reactions.indexOf(mostSelected)) {
        0 -> context.getString(R.string.emoji_fearfull)
        1 -> context.getString(R.string.emoji_hands)
        2 -> context.getString(R.string.emoji_angry)
        else -> context.getString(R.string.emoji_heart)
    }
}

fun setPopupWindowReactions(
    popupWindowView: View?,
    fearfulFaceReactions: Int?,
    handsPressedReactions: Int?,
    redAngryFaceReactions: Int?,
    redHeartReactions: Int?
) {
    popupWindowView?.reaction1_count?.text = fearfulFaceReactions.toString()
    popupWindowView?.reaction2_count?.text = handsPressedReactions.toString()
    popupWindowView?.reaction3_count?.text = redAngryFaceReactions.toString()
    popupWindowView?.reaction4_count?.text = redHeartReactions.toString()
}