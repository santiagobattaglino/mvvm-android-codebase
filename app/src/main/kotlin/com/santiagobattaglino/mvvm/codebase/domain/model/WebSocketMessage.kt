package com.santiagobattaglino.mvvm.codebase.domain.model

data class WebSocketMessage(val event: String = "", val data: WebSocketMessageData? = null) {
    companion object {
        const val EVENT_JOIN = "joinLiveStream"
        const val EVENT_LEAVE = "leaveLiveStream"
        const val EVENT_STOP = "stopLiveStream"
        const val MESSAGE_STOP_HQ = "stop_hq"
        const val MESSAGE_STOP_USER = "stop_user"
        const val MESSAGE_ALREADY_JOINED = "Not joined, already joined"

        // we're not using this for callbacks from agora for now
        const val MESSAGE_STOP_AGORA = "stop_agora"
    }
}

data class WebSocketMessageData(val liveStream: String = "", val message: String? = null)