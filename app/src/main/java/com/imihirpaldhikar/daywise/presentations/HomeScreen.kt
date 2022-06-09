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
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.imihirpaldhikar.daywise.R
import com.imihirpaldhikar.daywise.components.DeleteNoteDialog
import com.imihirpaldhikar.daywise.components.NoteCard
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
    val isDarkMode = isSystemInDarkTheme()

    if (homeState.showSortDialog) {
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

    if (homeState.showDeleteDialog) {
        DeleteNoteDialog(onDismiss = {
            homeViewModel.onEvent(HomeEvent.ShowDeleteDialog(false, null))
        }, onDelete = {
            homeViewModel.onEvent(HomeEvent.DeleteNote)
            homeViewModel.onEvent(HomeEvent.ShowDeleteDialog(false, null))
        }, onCancel = {
            homeViewModel.onEvent(HomeEvent.ShowDeleteDialog(false, null))
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
                    Text(text = "Daywise", fontWeight = FontWeight.Bold)
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

            SwipeRefresh(state = rememberSwipeRefreshState(homeState.isRefreshing), onRefresh = {
                homeViewModel.onEvent(HomeEvent.RefreshNotes)
            }) {
                if (notes.isEmpty()) {
                    return@SwipeRefresh Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painterResource(id = R.drawable.empty),
                            contentDescription = "No Notes"
                        )
                        Text(
                            text = "No Notes!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.W600,
                            fontSize = 25.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Create new notes by clicking on the '+'")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            end = 15.dp,
                            start = 15.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Sorting By : ",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = homeViewModel.sortOptions[homeViewModel.selectedSort].sortName,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        items(notes.size) {
                            NoteCard(
                                note = notes[it],
                                modified = homeViewModel.getCountOfDays(notes[it].updatedOn),
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
                                onDelete = {
                                    homeViewModel.onEvent(
                                        HomeEvent.ShowDeleteDialog(
                                            true,
                                            notes[it]
                                        )
                                    )
                                }, isDarkMode = isDarkMode
                            )
                        }
                    })
            }
        }

    }

}