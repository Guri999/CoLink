package com.seven.colink.domain.model

enum class NotifyType(
    val title: String
) {
    CHAT(
        title = "chat"
    ),
    INVITE(
        title = "invite"
    ),

    ;

    companion object {
        fun fromTitle (title: String) = entries.find {
            it.title == title
        }
    }
}
