package com.nafi.nafstory.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nafi.nafstory.R
import com.nafi.nafstory.data.remote.model.home.ListStory
import com.nafi.nafstory.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fetchData()
    }

    private fun fetchData() {
        intent.getParcelableExtra<ListStory>(EXTRA_ITEM)?.let { item ->
            binding.apply {
                tvName.text = item.name
                tvDescription.text = item.description
                Glide.with(this@DetailActivity)
                    .load(item.photoUrl)
                    .into(imgPhotos)
            }
        } ?: finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_ITEM = "extra_item"
    }
}
