package com.rerere.iwara4a.ui.component.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Space间距大小
 */
enum class SpaceSize(val size: Dp) {
    Small(4.dp),
    Normal(8.dp),
    Large(16.dp),
    Huge(32.dp);
}

/**
 * 自动为子组件添加间距
 *
 * @param modifier Modifier
 * @param vertical 是否垂直排列
 * @param size 间距大小
 * @param content 子组件
 */
@Composable
fun Space(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    size: SpaceSize = SpaceSize.Normal,
    content: @Composable () -> Unit
) {
    if (vertical) {
        VerticalSpace(modifier, size, content)
    } else {
        HorizontalSpace(modifier, size, content)
    }
}

@Composable
private fun VerticalSpace(
    modifier: Modifier,
    size: SpaceSize,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(size.size)
    ) {
        content()
    }
}


@Composable
private fun HorizontalSpace(
    modifier: Modifier,
    size: SpaceSize,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(size.size)
    ) {
        content()
    }
}