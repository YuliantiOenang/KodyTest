package com.yulianti.kodytest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yulianti.kodytest.databinding.FragmentCharacterListBinding
import com.yulianti.kodytest.ui.viewmodel.CharacterListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class CharacterListFragment : Fragment() {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharacterListViewModel by viewModels()

    private var _adapter: CharacterListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        val keyword = arguments?.getString("keyword")
        if (keyword?.isNotEmpty() == true) {
            viewModel.getCharacter(keyword)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        if (_adapter == null) {
            viewModel.getCharacter()
        }
        binding.recyclerView.apply {
            adapter = this@CharacterListFragment.getAdapter()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            // Collect the Flow when the lifecycle is at least STARTED
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characterFlow.collect { data ->
                    if (data is CharacterListViewModel.CharacterUiState.Loading) {
                        binding.loadingView.show()
                    } else {
                        if (data is CharacterListViewModel.CharacterUiState.Success) {
                            _adapter?.submitList(data.feed)
                            binding.loadingView.hide()
                            binding.recyclerView.visibility = View.VISIBLE
                        } else if (data is CharacterListViewModel.CharacterUiState.Error) {
                            println("yulianti error ${data.error}")
                        }
                    }
                }
            }
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (recyclerView.layoutManager as? LinearLayoutManager)?.let {
                    val lastVisibleItemPosition: Int = it.findLastVisibleItemPosition()
                    val totalItemCount = it.itemCount

//                    if (lastVisibleItemPosition + 1 == totalItemCount && !viewModel.isLoading() && !viewModel.isAllDataLoaded()) {
//                        viewModel.loadMore()
//                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAdapter(): CharacterListAdapter {
        if (_adapter == null) _adapter = CharacterListAdapter()
        return requireNotNull(_adapter)
    }
}