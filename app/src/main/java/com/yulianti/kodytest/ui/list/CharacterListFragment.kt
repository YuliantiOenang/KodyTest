package com.yulianti.kodytest.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yulianti.kodytest.databinding.FragmentCharacterListBinding
import com.yulianti.kodytest.util.asUiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

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

    private var keyword: String? = null
        set(value) {
            viewModel.getCharacter(value)
            field = value
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
        if (_adapter == null) {
            viewModel.getCharacter()
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
                        binding.loadingView.hide()
                        if (data?.error != null) {
                            Toast.makeText(requireContext(), requireContext().getString(data.error.asUiText()), Toast.LENGTH_SHORT).show()
                            println("yulianti error dari list ${data.error}")
                        } else {
                            println("yulianti items not empty")
                            _adapter?.submitList(data?.items?.items)
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }


        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        binding.searchView.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                println("yulianti close clicked")
                keyword = ""
                return true
            }
        })

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

    private fun performSearch(query: String) {
        Timber.d("yulianti perform search ${query}")
        keyword = query
    }
}