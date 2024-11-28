package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.story.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private val adapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        observeUserSession()

        observeStoryList()

        setupFabButtons()
    }

    private fun setupRecyclerView() {
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = adapter
    }

    private fun observeUserSession() {
        showLoading(true)
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToWelcome()
                return@observe
            }
            val token = user.token
            viewModel.getStory(token)
        }
    }

    private fun observeStoryList() {
        viewModel.listStories.observe(this) { stories ->
            if (stories != null && stories.isNotEmpty()) {
                adapter.setStories(stories)
            }
            showLoading(false)
        }
    }

    private fun setupFabButtons() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

        binding.fabLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
