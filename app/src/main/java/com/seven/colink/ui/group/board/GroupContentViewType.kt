package com.seven.colink.ui.group.board

enum class GroupContentViewType {
    GROUP_ITEM,
    POST_ITEM,
    MEMBER_ITEM,
    TITLE,
    SUB_TITLE,
    UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): GroupContentViewType = GroupContentViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}