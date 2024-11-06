package com.contentprovider.humans.presentation.list.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.contentprovider.humans.R
import com.contentprovider.humans.domain.entities.Human
import com.kevinnzou.compose.swipebox.SwipeBox
import com.kevinnzou.compose.swipebox.SwipeDirection
import com.kevinnzou.compose.swipebox.widget.SwipeIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalWearMaterialApi
@Composable
fun HumanDataListView(
    data: List<Human>,
    onClickItem: (Human) -> Unit,
    onDeleteItem: (Human) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = data, key = { it.id }) {
            val id = it.id
            val name = it.name
            val surname = it.surname
            val age = it.age

            val value = stringResource(id = R.string.user_data, id, name, surname, age)

            SwipeBox(modifier = Modifier
                .fillMaxWidth()
                .animateItem(),
                swipeDirection = SwipeDirection.EndToStart,
                endContentWidth = 60.dp,
                endContent = { swipeableState, _ ->
                    SwipeIcon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(id = R.string.delete),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        background = MaterialTheme.colorScheme.primary,
                        weight = 1f,
                        iconSize = 24.dp
                    ) {
                        coroutineScope.launch {
                            swipeableState.animateTo(0)
                        }
                        onDeleteItem(it)
                    }
                }) { _, _, _ ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClickItem(it) }
                ) {
                    Text(
                        value,
                        modifier = Modifier
                            .padding(all = 16.dp)
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}