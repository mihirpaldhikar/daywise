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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imihirpaldhikar.daywise.data.models.parcelize.NoteParcel
import com.imihirpaldhikar.daywise.events.NoteEvent
import com.imihirpaldhikar.daywise.states.NoteOperationState
import com.imihirpaldhikar.daywise.viewmodels.NoteViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navigator: DestinationsNavigator,
    noteParcel: NoteParcel? = null
) {
    val context = LocalContext.current
    val noteViewModel = hiltViewModel<NoteViewModel>()
    val noteState = noteViewModel.noteState
    val noteStatus = noteViewModel.noteStatus

    LaunchedEffect(noteViewModel, context) {
        if (noteParcel != null) {
            noteViewModel.onEvent(NoteEvent.LoadNote(noteParcel.noteId))
        }
        noteStatus.collect { state ->
            when (state) {
                is NoteOperationState.Settled -> {}
                is NoteOperationState.Saved -> {}
                is NoteOperationState.Failed -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    TextField(
                        value = noteState.title,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        placeholder = {
                            Text(text = "Title...")
                        },
                        onValueChange = { noteViewModel.onEvent(NoteEvent.NoteTitleChanged(it)) })
                },
                actions = {
                    IconButton(
                        enabled = noteState.content.isNotEmpty() && noteState.title.isNotEmpty(),
                        onClick = {
                            if (noteParcel != null) {
                                noteViewModel.onEvent(NoteEvent.UpdateNote(noteParcel.noteId))
                            } else {
                                noteViewModel.onEvent(NoteEvent.SaveNote)
                            }
                            navigator.navigateUp()
                        }) {
                        Icon(
                            if (noteParcel !== null) {
                                Icons.Outlined.Check
                            } else {
                                Icons.Outlined.Save
                            }, contentDescription = "Save",
                            tint = if (noteState.content.isNotEmpty() && noteState.title.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.navigateUp()
                    }) {
                        Icon(
                            if (noteParcel != null) Icons.Outlined.Close else Icons.Outlined.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(content = {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        TextField(
                            value = noteState.description,
                            placeholder = {
                                Text(text = "Description...")
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                            ),
                            onValueChange = {
                                noteViewModel.onEvent(
                                    NoteEvent.NoteDescriptionChanged(
                                        it
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                item {
                    TextField(
                        value = noteState.content,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(text = "Add your note...")
                        },
                        onValueChange = { noteViewModel.onEvent(NoteEvent.NoteContentChanged(it)) })
                }
            })
        }
    }
}