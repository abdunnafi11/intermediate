package com.nafi.nafstory.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.nafi.nafstory.R
import com.nafi.nafstory.data.DataRepository
import com.nafi.nafstory.data.remote.network.ApiClient
import com.nafi.nafstory.databinding.ActivityLoginBinding
import com.nafi.nafstory.utils.Message
import com.nafi.nafstory.utils.NetworkResult
import com.nafi.nafstory.utils.PrefsManager
import com.nafi.nafstory.utils.ViewModelFactory
import com.nafi.nafstory.viewmodel.LoginAndRegisterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: LoginAndRegisterViewModel
    private lateinit var prefsManager: PrefsManager
    private var loginJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        prefsManager = PrefsManager(this)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataRepository)
        )[LoginAndRegisterViewModel::class.java]
        setLogin()
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        playPropertyAnimation()
    }

    private fun playPropertyAnimation() {
        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.textView2, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtPass = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnSignIn = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, edtEmail, edtPass, btnSignIn)
            start()
        }
    }

    private fun setLogin() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Message.setMessage(this, getString(R.string.warning_input))
            } else {
                setLoadingState(true)
                lifecycle.coroutineScope.launchWhenResumed {
                    if (loginJob.isActive) loginJob.cancel()
                    loginJob = launch {
                        viewModel.login(email, password).collect{ result ->
                            when (result) {
                                is NetworkResult.Success -> {
                                    prefsManager.exampleBoolean = !result.data?.error!!
                                    prefsManager.token = result.data.result.token
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                    finish()
                                    setLoadingState(false)
                                }
                                is NetworkResult.Loading -> {
                                    setLoadingState(true)
                                }

                                is NetworkResult.Error -> {
                                    Message.setMessage(this@LoginActivity, resources.getString(R.string.check))
                                    setLoadingState(false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(loading: Boolean) {
        binding.btnSignIn.visibility = if (loading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}