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

/*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
*/
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.imihirpaldhikar.daywise.components.DeleteNoteDialog
import com.imihirpaldhikar.daywise.data.models.parcelize.NoteParcel
import com.imihirpaldhikar.daywise.events.HomeEvent
import com.imihirpaldhikar.daywise.events.NoteEvent
import com.imihirpaldhikar.daywise.presentations.destinations.NoteScreenDestination
import com.imihirpaldhikar.daywise.states.NoteOperationState
import com.imihirpaldhikar.daywise.utils.ColorManager
import com.imihirpaldhikar.daywise.viewmodels.NoteViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = NonDismissableDialog::class)
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
    val isDarkMode = isSystemInDarkTheme()

    LaunchedEffect(noteViewModel, context) {
        if (noteParcel != null) {
            noteViewModel.onEvent(NoteEvent.LoadNote(noteParcel.noteId))
            noteViewModel.onEvent(NoteEvent.EditNote(false))
        } else {
            noteViewModel.onEvent(NoteEvent.EditNote(true))
        }
        noteStatus.collect { state ->
            when (state) {
                is NoteOperationState.Settled -> {}
                is NoteOperationState.Saved -> {}
                is NoteOperationState.Failed -> {}
            }
        }
    }

    if (noteState.showDeleteDialog) {
        DeleteNoteDialog(onDismiss = {
            noteViewModel.onEvent(NoteEvent.ShowDeleteDialog(false))
        }, onDelete = {
            noteViewModel.onEvent(NoteEvent.DeleteNote(noteParcel!!.noteId, navigator))
            noteViewModel.onEvent(NoteEvent.ShowDeleteDialog(false))
        }, onCancel = {
            noteViewModel.onEvent(NoteEvent.ShowDeleteDialog(false))
        })
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SmallTopAppBar(
                title = {},
                actions = {

                    if (!noteState.isLoading) {
                        if (!noteState.enableEditing) {
                            IconButton(
                                enabled = noteState.content.isNotEmpty() && noteState.title.isNotEmpty(),
                                onClick = {
                                    noteViewModel.onEvent(NoteEvent.ShowDeleteDialog(true))
                                }) {
                                Icon(
                                    Icons.Outlined.Delete, contentDescription = "Save",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                            IconButton(
                                enabled = noteState.content.isNotEmpty() && noteState.title.isNotEmpty(),
                                onClick = {
                                    noteViewModel.onEvent(NoteEvent.EditNote(true))
                                }) {
                                Icon(
                                    Icons.Outlined.Edit, contentDescription = "Save",
                                    tint = if (noteState.content.isNotEmpty() && noteState.title.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        if (noteState.enableEditing) {
                            IconButton(
                                enabled = noteState.content.isNotEmpty() && noteState.title.isNotEmpty(),
                                onClick = {
                                    if (noteParcel != null) {
                                        noteViewModel.onEvent(NoteEvent.UpdateNote(noteParcel.noteId))
                                    } else {
                                        noteViewModel.onEvent(NoteEvent.SaveNote)
                                    }
                                    noteViewModel.onEvent(NoteEvent.Close(navigator))
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
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        noteViewModel.onEvent(NoteEvent.Close(navigator))
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
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (noteState.isLoading) {
                return@Box CircularProgressIndicator()
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                content = {
                    if (noteState.enableEditing) {
                        item {
                            LazyRow(
                                modifier = Modifier.padding(
                                    top = 20.dp,
                                    bottom = 20.dp
                                ),
                                content = {
                                    item {
                                        Spacer(modifier = Modifier.width(20.dp))
                                    }
                                    items(noteViewModel.priorityList.size) {
                                        val backGroundColor =
                                            if (isDarkMode) ColorManager.darkenColor(noteViewModel.priorityList[it].backgroundColor) else noteViewModel.priorityList[it].backgroundColor

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    Color(backGroundColor)
                                                )
                                                .padding(0.dp)
                                                .selectable(
                                                    selected = it == noteViewModel.selectedPriority,
                                                    onClick = {
                                                        noteViewModel.onEvent(
                                                            NoteEvent.TogglePriority(
                                                                noteViewModel.priorityList[it]
                                                            )
                                                        )
                                                    }
                                                ),
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(
                                                    end = 15.dp
                                                ),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Start
                                            ) {
                                                RadioButton(
                                                    selected = it == noteViewModel.selectedPriority,
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = Color(
                                                            ColorManager.getContrastColor(
                                                                backGroundColor
                                                            )
                                                        ),
                                                    ),
                                                    onClick = {
                                                        noteViewModel.onEvent(
                                                            NoteEvent.TogglePriority(
                                                                noteViewModel.priorityList[it]
                                                            )
                                                        )
                                                    }
                                                )
                                                Text(
                                                    text = noteViewModel.priorityList[it].priorityName,
                                                    modifier = Modifier.padding(top = 2.dp),
                                                    color = Color(
                                                        ColorManager.getContrastColor(
                                                            backGroundColor
                                                        )
                                                    ),
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                    item {
                                        Spacer(modifier = Modifier.width(20.dp))
                                    }
                                })
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                TextField(
                                    value = noteState.title,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                        disabledIndicatorColor = Color.Transparent
                                    ),
                                    textStyle = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.W500,
                                        fontSize = 25.sp
                                    ),
                                    singleLine = true,
                                    enabled = noteState.enableEditing,
                                    placeholder = {
                                        Text(
                                            text = "Title...",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.W500,
                                                fontSize = 25.sp
                                            ),
                                        )
                                    },
                                    onValueChange = {
                                        noteViewModel.onEvent(
                                            NoteEvent.NoteTitleChanged(
                                                it
                                            )
                                        )
                                    }
                                )
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                TextField(
                                    value = noteState.content,
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                        disabledIndicatorColor = Color.Transparent
                                    ),
                                    enabled = noteState.enableEditing,
                                    placeholder = {
                                        Text(
                                            text = "Add your note...",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                                        )
                                    },
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChange = {
                                        noteViewModel.onEvent(
                                            NoteEvent.NoteContentChanged(
                                                it
                                            )
                                        )
                                    },
                                )
                            }
                        }
                    } else {
                        item {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = noteState.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.W500,
                                        fontSize = 30.sp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        lineHeight = 35.sp
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 15.dp)
                                ) {
                                    Text(
                                        text = noteViewModel.formatDate(noteState.updatedOn),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Box(
                                        modifier = Modifier.padding(start = 35.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(noteState.priority.backgroundColor))
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "${noteState.priority.priorityName} Priority",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 30.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(0.5.dp)
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                        .clip(
                                            RoundedCornerShape(15.dp)
                                        )
                                )
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp)
                            ) {
                                Text(
                                    text = noteState.content,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    lineHeight = 35.sp
                                )
                            }
                        }
                    }
                })
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
object NonDismissableDialog : DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when (initialState.appDestination()) {
            NoteScreenDestination ->
                fadeIn(
                    initialAlpha = 1.0f,
                    animationSpec = tween(700)
                )
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            NoteScreenDestination ->
                fadeOut(

                    animationSpec = tween(700)
                )
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {

        return when (initialState.appDestination()) {
            NoteScreenDestination ->
                fadeIn(
                    initialAlpha = 1.0f,
                    animationSpec = tween(700)
                )
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            NoteScreenDestination ->
                fadeOut(
                    animationSpec = tween(700)
                )
            else -> null
        }
    }
}