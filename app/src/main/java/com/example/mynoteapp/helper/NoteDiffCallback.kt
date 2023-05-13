package com.example.mynoteapp.helper

import androidx.recyclerview.widget.DiffUtil
import com.example.mynoteapp.database.Note

class NoteDiffCallback(private  val mOldNoteList: List<Note>, private val mNewNoteList: List<Note>)
    : DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return mOldNoteList.size
    }

    override fun getNewListSize(): Int {
        return mNewNoteList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldNoteList[oldItemPosition].id == mNewNoteList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldNoteList[oldItemPosition]
        val newEmployee = mNewNoteList[newItemPosition]
        return oldEmployee.nik == newEmployee.nik && oldEmployee.nama == newEmployee.nama
    }
}