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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imihirpaldhikar.daywise.data.models.parcelize.NoteParcel
import com.imihirpaldhikar.daywise.events.HomeEvent
import com.imihirpaldhikar.daywise.presentations.destinations.NoteScreenDestination
import com.imihirpaldhikar.daywise.viewmodels.HomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Destination(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {

    val context = LocalContext.current
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val homeState = homeViewModel.homeState
    val notes = homeState.notes

    LaunchedEffect(homeViewModel, context) {
        homeViewModel.onEvent(HomeEvent.LoadNotes)
    }

    if (homeViewModel.showSortDialog) {
        AlertDialog(onDismissRequest = {
            homeViewModel.onEvent(HomeEvent.ShowSortOptions(false))
        }, title = {
            Text(text = "Sort Notes By")
        }, confirmButton = {
            TextButton(onClick = {
                homeViewModel.onEvent(HomeEvent.SortNotes(homeViewModel.sortOptions[homeViewModel.selectedSort]))
                homeViewModel.onEvent(HomeEvent.ShowSortOptions(false))
            }) {
                Text(text = "Sort")
            }
        },
            text = {
                LazyColumn(content = {
                    items(homeViewModel.sortOptions.size) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = it == homeViewModel.selectedSort,
                                    onClick = {
                                        homeViewModel.onEvent(HomeEvent.ToggleSort(homeViewModel.sortOptions[it]))
                                    }
                                )
                        ) {
                            RadioButton(
                                selected = it == homeViewModel.selectedSort,
                                onClick = {
                                    homeViewModel.onEvent(HomeEvent.ToggleSort(homeViewModel.sortOptions[it]))
                                }
                            )
                            Text(
                                text = homeViewModel.sortOptions[it].sortName,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                        }
                    }
                })
            })
    }
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
        },
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "Daywise")
                },
                actions = {
                    IconButton(onClick = {
                        homeViewModel.onEvent(HomeEvent.ShowSortOptions(true))
                    }) {
                        Icon(Icons.Outlined.Sort, contentDescription = "Sort Notes")
                    }
                }
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            if (homeState.isLoading) {
                return@Box CircularProgressIndicator()
            }

            if (notes.isEmpty()) {
                return@Box Text(text = "No Notes")
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        end = 10.dp,
                        start = 10.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    items(notes.size) {
                        Column {
                            Card {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                navigator.navigate(
                                                    NoteScreenDestination(
                                                        noteParcel = NoteParcel(
                                                            noteId = notes[it].id,
                                                            isUpdate = true,
                                                        )
                                                    )
                                                )
                                            },
                                            onLongClick = {
                                                homeViewModel.onEvent(HomeEvent.DeleteNote(notes[it]))
                                            }
                                        )
                                        .padding(
                                            end = 15.dp,
                                            start = 15.dp,
                                            top = 15.dp,
                                            bottom = 10.dp,
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notes[it].title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.W500,
                                            fontSize = 20.sp,
                                            modifier = Modifier
                                                .padding(bottom = 5.dp)
                                                .fillMaxWidth(0.94f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(notes[it].priority.color))
                                        )
                                    }
                                    Text(
                                        text = notes[it].content,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = homeViewModel.getCountOfDays(notes[it].updatedOn),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                })
        }

    }

}