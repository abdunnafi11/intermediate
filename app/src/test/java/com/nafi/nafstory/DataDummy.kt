package com.nafi.nafstory

import com.nafi.nafstory.data.remote.model.home.ListStory

object DataDummy {
    fun generateDummyStoryEntity(): List<ListStory> {
        val storyList = ArrayList<ListStory>()
        for (i in 1..10) {
            val news = ListStory(
                id = "story-FvU4u0Vp2S3PMsFg",
                name = "Dimas",
                description = "Lorem Ipsum",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                lat = -10.212,
                lon = -16.002,
            )
            storyList.add(news)
        }
        return storyList
    }
}