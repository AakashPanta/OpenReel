package com.openreel.app.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openreel.app.data.model.SuggestedCreator
import com.openreel.app.data.model.TrendingTopic
import com.openreel.app.data.model.VideoPost
import com.openreel.app.ui.theme.ReelAccent
import com.openreel.app.ui.theme.ReelBlack
import com.openreel.app.ui.theme.ReelSurfaceAlt
import com.openreel.app.ui.theme.ReelTextMuted

@Composable
fun ExploreScreen(viewModel: ExploreViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Explore",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Search creators, hashtags, and videos across your community-owned feed.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReelTextMuted
                )
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::updateQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search creators, tags, topics") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }

        item {
            SectionTitle(
                title = "Trending now",
                subtitle = "Transparent discovery, not mystery ranking"
            )
            Spacer(Modifier.height(14.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    count = uiState.trendingTopics.size,
                    key = { index -> uiState.trendingTopics[index].id }
                ) { index ->
                    TrendingTopicCard(uiState.trendingTopics[index])
                }
            }
        }

        item {
            SectionTitle(
                title = "Recommended creators",
                subtitle = "Based on your follows and topic affinity"
            )
            Spacer(Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.suggestedCreators.forEach { creator ->
                    SuggestedCreatorCard(creator)
                }
            }
        }

        item {
            SectionTitle(
                title = "Featured grid",
                subtitle = "Curated content buckets for rapid discovery"
            )
            Spacer(Modifier.height(14.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                userScrollEnabled = false,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(460.dp)
            ) {
                items(uiState.featuredVideos) { video ->
                    ExploreVideoCard(video)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = ReelTextMuted
        )
    }
}

@Composable
private fun TrendingTopicCard(topic: TrendingTopic) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ReelSurfaceAlt),
        modifier = Modifier.width(140.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = topic.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = topic.posts,
                style = MaterialTheme.typography.bodyMedium,
                color = ReelTextMuted
            )
        }
    }
}

@Composable
private fun SuggestedCreatorCard(item: SuggestedCreator) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = ReelSurfaceAlt
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(ReelBlack),
                contentAlignment = Alignment.Center
            ) {
                Text(item.creator.avatarText, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.creator.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.creator.verified) {
                        Spacer(Modifier.size(6.dp))
                        Icon(
                            Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = ReelAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = "${item.creator.handle} • ${item.niche}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReelTextMuted
                )
                Text(
                    text = item.mutuals,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReelTextMuted
                )
            }
            Surface(shape = RoundedCornerShape(999.dp), color = ReelAccent) {
                Text(
                    text = "Follow",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = ReelBlack,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ExploreVideoCard(video: VideoPost) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ReelSurfaceAlt)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .background(Brush.verticalGradient(video.palette))
            ) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = ReelBlack.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = video.durationLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = video.creator.handle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReelTextMuted
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = video.tags.joinToString(separator = " • ") { "#$it" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReelTextMuted
                )
            }
        }
    }
}
