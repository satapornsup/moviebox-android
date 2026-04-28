package com.mastercoding.moviebox.presentation.home.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mastercoding.moviebox.domain.model.Movie
import com.mastercoding.moviebox.presentation.home.MovieListProvider

private const val LOAD_MORE_THRESHOLD = 5

@Composable
fun MovieList(
    movies: List<Movie>,
    isLoadingMore: Boolean,
    endReached: Boolean,
    onClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val total = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            total > 0 && lastVisible >= total - 1 - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(shouldLoadMore, endReached, isLoadingMore) {
        Log.d("MovieList", "shouldLoadMore=$shouldLoadMore, endReached=$endReached, isLoadingMore=$isLoadingMore")
        if (shouldLoadMore && !endReached && !isLoadingMore) {
            Log.d("MovieList", "→ onLoadMore()")
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(items = movies, key = { it.id }) { m -> MovieRow(m, onClick) }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (endReached && movies.isNotEmpty() && !isLoadingMore) {
            item {
                Text(
                    text = "—  end of list  —",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieListPreview(@PreviewParameter(MovieListProvider::class) movies: List<Movie>) {
    MovieList(
        movies = movies,
        isLoadingMore = false,
        endReached = true,
        onClick = {},
        onLoadMore = {},
    )
}
