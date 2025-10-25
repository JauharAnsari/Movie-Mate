package com.example.moviemate

import HomeViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviemate.ui.theme.MovieMateTheme
import org.koin.androidx.compose.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieMateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNav(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") {
            val vm: HomeViewModel = getViewModel()
            HomeScreen(
                viewModel = vm,
                onItemClick = { item ->

                    navController.navigate("details/${item.id}")
                }
            )
        }
        composable("details/{imdbId}") { backStackEntry ->
            val imdbId = backStackEntry.arguments?.getString("imdbId") ?: ""

            val vm: DetailsViewModel = getViewModel()
            val uiState by vm.uiState.collectAsState()


            LaunchedEffect(imdbId) {
                vm.loadDetails(imdbId)
            }


            when (val state = uiState) {
                is DetailsUiState.Loading -> {

                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading details...")
                    }
                }
                is DetailsUiState.Success -> {
                    DetailsScreen(
                        item = state.item,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                is DetailsUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${state.message}")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { vm.loadDetails(imdbId) }) {
                                Text("Retry")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { navController.popBackStack() }) {
                                Text("Go Back")
                            }
                        }
                    }
                }
            }
        }
    }
}
