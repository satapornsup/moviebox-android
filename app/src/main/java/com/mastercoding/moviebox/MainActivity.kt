package com.mastercoding.moviebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mastercoding.moviebox.presentation.nav.NavGraph
import com.mastercoding.moviebox.ui.theme.MovieBoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieBoxTheme {
                NavGraph()
            }
        }
    }
}