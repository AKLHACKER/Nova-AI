package com.example

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import com.example.ui.MainScreen
import com.example.ui.SchoolViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

