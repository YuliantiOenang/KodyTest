package com.yulianti.kodytest.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.yulianti.kodytest.databinding.FragmentCharacterDetailBinding
import com.yulianti.kodytest.util.asUiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CharacterDetailFragment : Fragment() {

    private var _binding: FragmentCharacterDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CharacterDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itemId = arguments?.getInt("itemId") ?: 0
        viewModel.getCharacterDetail(itemId)
        _binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characterFlow.collect { data ->
                    if (data?.isLoading == true) {
                        binding.loadingView.show()
                    } else {
                        if (data?.feed != null) {
                            binding.tvTitle.text = data.feed.name
                            val requestOption = RequestOptions().transform(CenterInside(), RoundedCorners(16))
                            Glide.with(requireContext())
                                .load(data.feed.coverUrl)
                                .transition(DrawableTransitionOptions.withCrossFade(200))
                                .apply(requestOption)
                                .into(binding.ivThumbnail)

                            binding.tvTitle.text = data.feed.name
                            binding.tvDescription.text = data.feed.description
                            binding.loadingView.hide()
                        } else if (data?.error != null) {
                            Toast.makeText(
                                requireContext(),
                                requireContext().getString(data.error.asUiText()),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}