package com.contentprovider.humans.presentation.list.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.contentprovider.humans.R
import com.contentprovider.humans.domain.entities.Human

@Composable
fun HumanDataListView(
    data: List<Human>,
    onClickItem: (Human) -> Unit,
    onDeleteItem: (Human) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = data, key = { it.id }) {
            val id = it.id
            val name = it.name
            val surname = it.surname
            val age = it.age

            val value = stringResource(id = R.string.user_data, id, name, surname, age)

            Column(
                modifier = Modifier.animateItem()
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(value,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onClickItem(it) }
                            .padding(all = 16.dp))
                    IconButton(onClick = { onDeleteItem(it) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = null,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}