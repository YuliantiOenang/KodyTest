package com.yulianti.kodytest.ui

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
    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private val keyword: String?
        get() {
            val keyword = arguments?.getString("keyword")
            return keyword
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_adapter == null || keyword?.isNotEmpty() == true) {
            viewModel.getCharacter(keyword)
        }
        binding.recyclerView.apply {
            adapter = this@CharacterListFragment.getAdapter()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characterFlow
                    .collect { data ->
                    if (data?.isLoading == true) {
                        binding.loadingView.show()
                    } else {
                        if (data?.items?.items?.isNotEmpty() == true) {
                            println("yulianti items not empty")
                            _adapter?.submitList(data.items.items)
                            binding.loadingView.hide()
                            binding.recyclerView.visibility = View.VISIBLE
                        } else if (data?.error != null && data.error != 0) {
                            Toast.makeText(requireContext(), requireContext().getString(data.error), Toast.LENGTH_SHORT).show()
                            println("yulianti error dari list ${data.error}")
                        }
                    }
                }
            }
        }

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                    val lastVisibleItemPosition: Int = layoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (lastVisibleItemPosition + 1 == totalItemCount && !(viewModel.characterFlow.value?.isLoading == true) && !viewModel.isAllDataLoaded()) {
                        println("yulianti load more")
                        viewModel.loadMore(keyword)
                    }
                }
            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onDestroyView() {
        binding.recyclerView.removeOnScrollListener(scrollListener)
        super.onDestroyView()
        _binding = null
    }

    private fun getAdapter(): CharacterListAdapter {
        if (_adapter == null) _adapter = CharacterListAdapter()
        return requireNotNull(_adapter)
    }
}