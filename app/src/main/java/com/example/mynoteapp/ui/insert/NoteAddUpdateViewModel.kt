package com.example.mynoteapp.ui.insert

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.mynoteapp.database.Note
import com.example.mynoteapp.repository.NoteRepository

class NoteAddUpdateViewModel(application: Application) : ViewModel() {

    private val mNoteRepository : NoteRepository = NoteRepository(application)

    fun insert(note: Note){
        mNoteRepository.insert(note)
    }

    fun delete(note: Note){
        mNoteRepository.delete(note)
    }

}