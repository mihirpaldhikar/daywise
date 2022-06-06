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

package com.imihirpaldhikar.daywise.data.repositories

import com.imihirpaldhikar.daywise.AppDatabase
import com.imihirpaldhikar.daywise.data.models.Note
import javax.inject.Inject

class NotesRepository @Inject constructor(
    appDatabase: AppDatabase
) {

    private val notesSource = appDatabase.notesDao()

    suspend fun getAllNotes(): List<Note> {
        return notesSource.getNotes()
    }

    suspend fun getNoteById(noteId: String): Note? {
        return notesSource.getNoteById(noteId)
    }

    suspend fun updateNote(note: Note) {
        return notesSource.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        return notesSource.deleteNote(note)
    }

    suspend fun addNote(note: Note) {
        return notesSource.addNote(note)
    }

}