package com.sonicmaster.herokuapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.sonicmaster.herokuapp.data.UserPreferences
import com.sonicmaster.herokuapp.ui.auth.AuthActivity
import com.sonicmaster.herokuapp.ui.home.HomeActivity
import com.sonicmaster.herokuapp.ui.startNewActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            UserPreferences.getToken(this@MainActivity).asLiveData().observe(this@MainActivity, {
                val activity =
                    if (it == "No token found") AuthActivity::class.java else HomeActivity::class.java
                startNewActivity(activity)

            })
        }
    }

}