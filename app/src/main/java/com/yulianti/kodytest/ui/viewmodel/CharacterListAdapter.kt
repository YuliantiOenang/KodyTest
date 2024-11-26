package com.yulianti.kodytest.ui.viewmodel

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class CharacterListAdapter: ListAdapter<Character, CharacterListAdapter.CharacterViewHolder>(DiffCallback()) {
    class CharacterViewHolder(itemView: View) : ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class DiffCallback: DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
            TODO("Not yet implemented")
        }
    }
}