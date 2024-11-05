package com.contentprovider.humans.presentation.list.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.contentprovider.humans.R
import com.contentprovider.humans.domain.entities.Human

@Composable
fun HumanDataListView(
    data: List<Human>,
    onClickItem: (Human) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(data) {
            val id = it.id
            val name = it.name
            val surname = it.surname
            val age = it.age

            val value = stringResource(id = R.string.user_data, id, name, surname, age)

            Column {
                Text(
                    value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClickItem(it) }
                        .padding(all = 16.dp)
                )
                HorizontalDivider()
            }
        }
    }
}