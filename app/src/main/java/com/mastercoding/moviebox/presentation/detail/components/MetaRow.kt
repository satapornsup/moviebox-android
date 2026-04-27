package com.mastercoding.moviebox.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastercoding.moviebox.ui.theme.MovieBoxTheme

@Composable
fun MetaRow(release: String?, rating: Double) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (release != null) {
            Text(release, style = MaterialTheme.typography.bodySmall)
        }
        Text("★ %.1f".format(rating), style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
private fun MetaRowPreview() {
    MovieBoxTheme {
        MetaRow(
            release = "2022-01-01",
            rating = 3.3,
        )
    }
}