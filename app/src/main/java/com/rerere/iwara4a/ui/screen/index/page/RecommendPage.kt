package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.data.model.detail.video.toMediaPreview
import com.rerere.iwara4a.ui.component.MediaPreviewCard
import com.rerere.iwara4a.ui.component.RandomLoadingAnim
import com.rerere.iwara4a.ui.component.layout.Centered
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.ui.util.adaptiveStaggeredGridCell
import com.rerere.iwara4a.ui.util.stringResourceByName
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.onError
import com.rerere.iwara4a.util.onLoading
import com.rerere.iwara4a.util.onSuccess
import me.rerere.compose_setting.preference.rememberStringSetPreference

@Composable
fun RecommendPage(
    indexViewModel: IndexViewModel
) {
    fun refresh(tags: Set<String>) {
        indexViewModel.recommendVideoList(tags)
    }

    val allTags by indexViewModel.allRecommendTags.collectAsState()
    var tags by rememberStringSetPreference(
        key = "recommend_tag",
        default = emptySet()
    )
    val recommendVideoList by indexViewModel.recommendVideoList.collectAsState()
    val refreshState = rememberSwipeRefreshState(recommendVideoList is DataState.Loading)
    var showSettingDialog by remember {
        mutableStateOf(false)
    }

    if (showSettingDialog) {
        AlertDialog(
            onDismissRequest = { showSettingDialog = false },
            title = {
                Text("推荐标签设置")
            },
            text = {
                allTags
                    .onSuccess {
                        FlowRow(
                            mainAxisSpacing = 2.dp
                        ) {
                            it.forEach {
                                FilterChip(
                                    selected = tags.contains(it),
                                    onClick = {
                                        tags = if (tags.contains(it)) {
                                            tags.toMutableSet().apply {
                                                remove(it)
                                            }
                                        } else {
                                            tags.toMutableSet().apply {
                                                add(it)
                                            }
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = stringResourceByName("tag_$it")
                                        )
                                    }
                                )
                            }
                        }
                    }.onLoading {
                        Centered(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator()
                        }
                    }.onError {
                        Centered(
                            Modifier
                                .clickable {
                                    indexViewModel.loadTags()
                                }
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "获取标签失败, 点击重试 ($it)"
                            )
                        }
                    }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSettingDialog = false
                        refresh(tags)
                    }
                ) {
                    Text(stringResource(R.string.confirm_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSettingDialog = false }
                ) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    LaunchedEffect(tags) {
        if (recommendVideoList is DataState.Empty) {
            refresh(tags)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showSettingDialog = true
                }
            ) {
                Icon(Icons.Outlined.Settings, null)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        if (tags.isEmpty()) {
            Centered(Modifier.fillMaxSize()) {
                Text("请先选择你喜欢的标签")
            }
        } else {
            SwipeRefresh(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = refreshState,
                onRefresh = {
                    indexViewModel.recommendVideoList(tags)
                }
            ) {
                Crossfade(recommendVideoList) { recommendVideoList ->
                    when (recommendVideoList) {
                        is DataState.Success -> {
                            LazyVerticalStaggeredGrid(
                                columns = adaptiveStaggeredGridCell()
                            ) {
                                items(recommendVideoList.readSafely() ?: emptyList()) {
                                    MediaPreviewCard(
                                        dynamicHeight = true,
                                        mediaPreview = it.toMediaPreview()
                                    )
                                }
                            }
                        }
                        is DataState.Loading -> {
                            RandomLoadingAnim()
                        }
                        is DataState.Error -> {
                            Centered(Modifier.fillMaxSize()) {
                                Icon(Icons.Outlined.Error, null)
                            }
                        }
                        is DataState.Empty -> {}
                    }
                }
            }
        }
    }
}