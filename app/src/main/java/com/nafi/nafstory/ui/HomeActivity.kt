package com.nafi.nafstory.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nafi.nafstory.R
import com.nafi.nafstory.data.DataRepository
import com.nafi.nafstory.data.remote.model.home.ListStory
import com.nafi.nafstory.data.remote.network.ApiClient
import com.nafi.nafstory.databinding.ActivityHomeBinding
import com.nafi.nafstory.ui.adapter.StoryAdapter
import com.nafi.nafstory.utils.Message
import com.nafi.nafstory.utils.PrefsManager
import com.nafi.nafstory.utils.ViewModelFactory
import com.nafi.nafstory.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity(), StoryAdapter.OnItemClickAdapter {
    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private lateinit var prefsManager: PrefsManager
    private lateinit var viewModel: HomeViewModel
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        prefsManager = PrefsManager(this)
        storyAdapter = StoryAdapter(this, this)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(this, ViewModelFactory(dataRepository))[HomeViewModel::class.java]
        fetchData(prefsManager.token)

        viewModel.getStory(prefsManager.token)

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            fetchData(prefsManager.token)
        }

        binding.btnTry.setOnClickListener {
            setLoadingState(true)
            fetchData(prefsManager.token)
        }

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun fetchData(auth: String) {
        binding.rvStory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = storyAdapter
        }
        viewModel.apply {
            setLoadingState(true)
            viewModel.quote.observe(this@HomeActivity, {
                setLoadingState(false)

                storyAdapter.submitData(lifecycle, it)
                binding.swipeRefresh.isRefreshing = false

            })

            setLoadingState(false)

        }
    }
    private fun setLoadingState(loading: Boolean) {
        when(loading) {
            true -> {
                binding.rvStory.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            false -> {
                binding.rvStory.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle(resources.getString(R.string.log_out))
                dialog.setMessage(getString(R.string.are_you_sure))
                dialog.setPositiveButton(getString(R.string.yes)) {_,_ ->
                    prefsManager.clear()
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                    this@HomeActivity.finish()
                    Message.setMessage(this, getString(R.string.log_out_warning))
                }
                dialog.setNegativeButton(getString(R.string.no)) {_,_ ->
                    Message.setMessage(this, getString(R.string.not_out))
                }
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onItemClicked(listStory: ListStory, optionsCompat: ActivityOptionsCompat) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_ITEM, listStory)
        startActivity(intent, optionsCompat.toBundle())
    }

}