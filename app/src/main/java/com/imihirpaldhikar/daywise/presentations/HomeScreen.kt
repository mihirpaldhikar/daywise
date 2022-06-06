/*
 * Copyright 2022 Mihir Paldhikar
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.imihirpaldhikar.daywise.presentations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imihirpaldhikar.daywise.data.models.parcelize.NoteParcel
import com.imihirpaldhikar.daywise.presentations.destinations.NoteScreenDestination
import com.imihirpaldhikar.daywise.viewmodels.HomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val notes = homeViewModel.notes
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigator.navigate(
                    NoteScreenDestination(
                        noteParcel = null
                    )
                )
            }) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.padding(
                    end = 10.dp,
                    start = 10.dp,
                    top = 20.dp
                ),
                content = {
                    if (notes.isEmpty()) {
                        return@LazyColumn item {
                            Text(text = "No Notes!")
                        }
                    }
                    items(notes.size) {
                        Column {
                            Card {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navigator.navigate(
                                                NoteScreenDestination(
                                                    noteParcel = NoteParcel(
                                                        noteId = notes[it].id,
                                                        isUpdate = true,
                                                    )
                                                )
                                            )
                                        }
                                        .padding(
                                            end = 15.dp,
                                            start = 15.dp,
                                            top = 20.dp,
                                            bottom = 20.dp,
                                        )
                                ) {
                                    Text(
                                        text = notes[it].title,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 5.dp),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = notes[it].description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 4,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                })
        }
    }

}