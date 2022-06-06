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

package com.imihirpaldhikar.daywise.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imihirpaldhikar.daywise.data.models.database.Note
import com.imihirpaldhikar.daywise.data.repositories.NotesRepository
import com.imihirpaldhikar.daywise.events.NoteEvent
import com.imihirpaldhikar.daywise.states.NoteDataState
import com.imihirpaldhikar.daywise.states.NoteOperationState
import com.imihirpaldhikar.daywise.utils.RandomUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    var noteState by mutableStateOf(NoteDataState())
    private val noteChannel = Channel<NoteOperationState<Unit>>()
    val noteStatus = noteChannel.receiveAsFlow()

    fun onEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.NoteTitleChanged -> {
                noteState = noteState.copy(title = event.title)
            }
            is NoteEvent.NoteDescriptionChanged -> {
                noteState = noteState.copy(description = event.description)
            }
            is NoteEvent.NoteContentChanged -> {
                noteState = noteState.copy(content = event.content)
            }
            is NoteEvent.SaveNote -> {
                viewModelScope.launch {
                    val systemTime = System.currentTimeMillis()
                    val rId = RandomUID.generate
                    val noteData = Note(
                        title = noteState.title.trim(),
                        description = noteState.description.trim(),
                        content = noteState.content.trim(),
                        createdOn = systemTime,
                        updatedOn = systemTime,
                        id = UUID.randomUUID().toString()
                    )
                    notesRepository.addNote(noteData)
                }
            }
            is NoteEvent.UpdateNote -> {
                viewModelScope.launch {
                    val systemTime = System.currentTimeMillis()
                    val noteData = notesRepository.getNoteById(event.noteId)!!.copy(
                        title = noteState.title.trim(),
                        description = noteState.description.trim(),
                        content = noteState.content.trim(),
                        updatedOn = systemTime
                    )
                    notesRepository.updateNote(noteData)
                }
            }
            is NoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    notesRepository.deleteNote(event.note)
                }
            }
            is NoteEvent.LoadNote -> {
                viewModelScope.launch {
                    val noteData = notesRepository.getNoteById(event.noteId)
                    noteState = noteState.copy(
                        title = noteData!!.title,
                        description = noteData.description,
                        content = noteData.content
                    )
                }
            }
        }
    }
}