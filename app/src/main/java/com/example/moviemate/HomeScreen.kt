package com.example.moviemate

import HomeViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.material.placeholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onItemClick: (MediaItem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isMovies by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        viewModel.load(isMovies)
    }


    LaunchedEffect(isMovies) {
        viewModel.load(isMovies)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MovieMate", textAlign = TextAlign.Center) }

            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Movie and Tv Shows Toggle Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF37474F)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Movies
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (isMovies) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { isMovies = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Movies",
                            color = if (isMovies) Color.White else Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // TV Shows
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (!isMovies) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { isMovies = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TV Shows",
                            color = if (!isMovies) Color.White else Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }


            when (val state = uiState) {
                is UiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        items(6) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(120.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp, 120.dp)
                                        .placeholder(visible = true)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(verticalArrangement = Arrangement.Center) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .height(24.dp)
                                            .placeholder(visible = true)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .height(20.dp)
                                            .placeholder(visible = true)
                                    )
                                }
                            }
                        }
                    }
                }

                is UiState.Success -> {
                    val items = state.items
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No items available.")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(items, key = { it.id }) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                        .clickable { onItemClick(item) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        // Movie Image
                                        AsyncImage(
                                            model = item.finalPosterUrl ?: item.placeholderPosterUrl,
                                            contentDescription = "Poster for ${item.title}",
                                            modifier = Modifier
                                                .size(width = 80.dp, height = 120.dp)
                                                .clip(MaterialTheme.shapes.small)
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))


                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            item.year?.let { year ->
                                                Text(
                                                    text = "Year: $year",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.load(isMovies) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}