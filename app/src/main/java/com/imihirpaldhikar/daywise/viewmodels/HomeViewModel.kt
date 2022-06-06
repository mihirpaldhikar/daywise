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
import com.imihirpaldhikar.daywise.events.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    var notes by mutableStateOf<List<Note>>(emptyList())

    init {
        viewModelScope.launch {
            notesRepository.getAllNotes().collect {
                notes = it
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.DeleteNote -> {
                viewModelScope.launch {
                    notesRepository.deleteNote(event.note)
                    notesRepository.getAllNotes().collect {
                        notes = it
                    }
                }
            }
        }
    }

    private fun formatDate(date: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        return formatter.format(Date(date))
    }

    fun getCountOfDays(createdDateString: Long): String {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val updatedOnDate = dateFormat.parse(formatDate(createdDateString))!!
        val systemDate = dateFormat.parse(formatDate(System.currentTimeMillis()))!!
        val difference = kotlin.math.abs(updatedOnDate.time - systemDate.time)
        val dayDifference = difference / (24 * 60 * 60 * 1000)

        val days = dayDifference.toString()
        return if (days == "0") {
            "Today"
        } else {
            if (days == "1") {
                "Yesterday"
            } else {
                "$days days ago"
            }
        }
    }

}