package com.nafi.nafstory.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nafi.nafstory.R
import com.nafi.nafstory.data.DataRepository
import com.nafi.nafstory.data.remote.network.ApiClient
import com.nafi.nafstory.databinding.ActivityRegisterBinding
import com.nafi.nafstory.utils.Message
import com.nafi.nafstory.utils.NetworkResult
import com.nafi.nafstory.utils.ViewModelFactory
import com.nafi.nafstory.viewmodel.LoginAndRegisterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: LoginAndRegisterViewModel
    private var registerJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.register)
            setDisplayHomeAsUpEnabled(true)
        }

        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(this, ViewModelFactory(dataRepository))
            .get(LoginAndRegisterViewModel::class.java)

        setViews()
        playPropertyAnimation()
    }

    private fun playPropertyAnimation() {
        val objectAnimators = listOf(
            ObjectAnimator.ofFloat(binding.imgRegister, View.TRANSLATION_X, -30f, 30f),
            ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f),
            ObjectAnimator.ofFloat(binding.textView2, View.ALPHA, 1f),
            ObjectAnimator.ofFloat(binding.edtUsername, View.ALPHA, 1f),
            ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f),
            ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f),
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f)
        )

        AnimatorSet().apply {
            playSequentially(objectAnimators)
            duration = 500
            start()
        }
    }

    private fun setViews() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = edtUsername.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
                    Message.setMessage(this@RegisterActivity, getString(R.string.warning_input))
                } else {
                    setLoadingState(true)
                    lifecycleScope.launch {
                        registerJob?.cancel()
                        registerJob = launch {
                            viewModel.register(name, email, password).collect { result ->
                                when(result) {
                                    is NetworkResult.Success -> {
                                        setLoadingState(false)
                                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                        Message.setMessage(this@RegisterActivity, getString(R.string.success_register))
                                        finish()
                                    }
                                    is NetworkResult.Loading -> {
                                        setLoadingState(true)
                                    }
                                    is NetworkResult.Error -> {
                                        Message.setMessage(this@RegisterActivity, getString(R.string.error_register))
                                        setLoadingState(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(loading: Boolean) {
        binding.apply {
            btnRegister.visibility = if (loading) View.INVISIBLE else View.VISIBLE
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
