package com.seven.colink.ui.mypage

sealed class MyPostItem {
    data class MyPagePostItem(val projecting: String, val projectName: String, val projectTime: String):MyPostItem()
    data class MyPageStudyItem(val studying: String, val studyName: String, val studyTime: String):MyPostItem()
}