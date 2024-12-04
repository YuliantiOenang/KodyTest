package com.yulianti.kodytest.ui.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.yulianti.kodytest.databinding.ItemCharacterBinding
import com.yulianti.kodytest.data.model.Character

class CharacterListAdapter: ListAdapter<Character, CharacterListAdapter.CharacterViewHolder>(
    DiffCallback()
) {
    class CharacterViewHolder(private val binding: ItemCharacterBinding) : ViewHolder(binding.root) {
        private val requestOption = RequestOptions().transform(CenterCrop(), RoundedCorners(16))

        @SuppressLint("SetTextI18n")
        fun bind(character: Character) {
            binding.root.context.let { context ->
                Glide.with(context)
                    .load(character.coverUrl)
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .apply(requestOption)
                    .into(binding.imgCover)

                binding.tvTitle.text = character.name

            }

            binding.root.setOnClickListener { view ->
                val action = CharacterListFragmentDirections.actionListFragmentToDetailFragment(character.id)
                view.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(
            ItemCharacterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class DiffCallback: DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem == newItem
        }
    }
}