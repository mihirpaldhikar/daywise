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

package com.imihirpaldhikar.daywise.events

import com.imihirpaldhikar.daywise.data.models.database.Note
import com.imihirpaldhikar.daywise.enums.NotePriority
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

sealed class NoteEvent {
    data class NoteTitleChanged(val title: String) : NoteEvent()
    data class NoteContentChanged(val content: String) : NoteEvent()
    data class UpdateNote(val noteId: String) : NoteEvent()
    data class DeleteNote(val note: Note) : NoteEvent()
    data class LoadNote(val noteId: String) : NoteEvent()
    data class TogglePriority(val priority: NotePriority) : NoteEvent()
    data class EditNote(val enable: Boolean) : NoteEvent()
    data class Close(val navigator: DestinationsNavigator) : NoteEvent()
    object SaveNote : NoteEvent()
}