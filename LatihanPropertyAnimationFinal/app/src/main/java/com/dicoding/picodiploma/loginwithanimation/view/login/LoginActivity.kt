package com.dicoding.picodiploma.loginwithanimation.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultValue
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.customviewmodel.buttonview
import com.dicoding.picodiploma.loginwithanimation.view.customviewmodel.emailview
import com.dicoding.picodiploma.loginwithanimation.view.customviewmodel.passwordview
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var password: passwordview
    private lateinit var button: buttonview
    private lateinit var email: emailview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.emailEditText
        password = binding.passwordEditText
        button = binding.loginButton

        setButtonState()

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setButtonState()
            }

            override fun afterTextChanged(s: Editable) {}
        }

        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        button.setOnClickListener { setupAction() }

        setupView()
        playAnimation()
    }

    private fun setButtonState() {
        val emailValid = email.text.toString().isNotEmpty() && email.error == null
        val passwordValid = password.text.toString().isNotEmpty() && password.error == null
        button.isEnabled = emailValid && passwordValid
    }

    private fun setupAction() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        viewModel.login(email, password).observe(this) { result ->
            when (result) {
                is ResultValue.Loading -> showLoading(true)
                is ResultValue.Success -> {
                    val token = result.data.toString()
                    viewModel.saveSession(UserModel(email, password, token))
                    showToast("Login Success")
                    showLoading(false)
                    navigateToMainActivity()
                }

                is ResultValue.Error -> {
                    showToast(result.error)
                    showLoading(false)
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val fadeInAnimations = listOf(
            binding.titleTextView, binding.messageTextView, binding.emailTextView,
            binding.emailEditTextLayout, binding.passwordTextView, binding.passwordEditTextLayout,
            binding.loginButton
        ).map { view ->
            ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(100)
        }

        AnimatorSet().apply {
            playSequentially(fadeInAnimations)
            startDelay = 100
        }.start()
    }
}
