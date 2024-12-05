package com.yulianti.kodytest.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchView
import com.yulianti.kodytest.R
import com.yulianti.kodytest.databinding.FragmentCharacterListBinding
import com.yulianti.kodytest.util.asUiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CharacterListFragment : Fragment() {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CharacterListViewModel by viewModels()

    private var _adapter: CharacterListAdapter? = null
    private var _searchAdapter: CharacterListAdapter? = null
    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private var keyword: String? = null
        set(value) {
            field = value
            viewModel.getCharacter(value)
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
        setupRecyclerViews()
        setupSearchBar()
        setupDataCollection()
    }

    override fun onResume() {
        super.onResume()
        resetSearchState()
    }

    override fun onDestroyView() {
        binding.recyclerView.removeOnScrollListener(scrollListener)
        super.onDestroyView()
        _binding = null
    }

    private fun resetSearchState() {
        with(binding) {
            if (searchView.isShowing) {
                searchView.hide()
                searchBar.setText("")
                searchView.editText.setText("")
                performSearch("")
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.recyclerView.apply {
            adapter = getMainAdapter()
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(createDividerDecoration())
            setupScrollListener(this)
        }

        binding.searchResultsRecyclerView.apply {
            adapter = getSearchAdapter()
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(createDividerDecoration())
        }
    }

    private fun createDividerDecoration(): DividerItemDecoration {
        return DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
            ContextCompat.getDrawable(requireContext(), R.drawable.divider)?.let {
                setDrawable(it)
            }
        }
    }

    private fun setupSearchBar() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)

            searchView.editText.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchQuery = textView.text.toString()
                    performSearch(searchQuery)
                    true
                } else {
                    false
                }
            }

            searchView.addTransitionListener { searchView, _, newState ->
                if (newState == SearchView.TransitionState.HIDDEN) {
                    searchView.editText.setText("")
                    getSearchAdapter().submitList(listOf())
                    performSearch("")
                }
            }
        }
    }

    private fun setupDataCollection() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characterFlow
                    .collect { data ->
                        handleDataState(data)
                    }
            }
        }
    }

    private fun handleDataState(data: CharacterListViewModel.CharacterUiState?) {
        with(binding) {
            when {
                data?.isLoading == true -> {
                    if (searchView.isShowing) {
                        searchLoadingView.show()
                    } else {
                        loadingView.show()
                    }
                }

                data?.error != null -> {
                    loadingView.hide()
                    searchLoadingView.hide()
                    showError(data.error.asUiText())
                }

                else -> {
                    loadingView.hide()
                    searchLoadingView.hide()
                    if (data?.items?.items.isNullOrEmpty() && viewModel.isEmpty()) {
                        showEmptyState()
                    }
                    if (searchView.isShowing && searchView.text.isNotEmpty()) {
                        _searchAdapter?.submitList(data?.items?.items)
                    } else {
                        _adapter?.submitList(data?.items?.items)
                    }
                }
            }
        }
    }

    private fun showError(error: Int) {
        Toast.makeText(
            requireContext(),
            requireContext().getString(error),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showEmptyState() {
        Toast.makeText(
            requireContext(),
            requireContext().getString(R.string.empty_list),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupScrollListener(recyclerView: RecyclerView) {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                    val lastVisibleItemPosition =
                        layoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    viewModel.onScroll(lastVisibleItemPosition, totalItemCount, keyword)
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun getMainAdapter(): CharacterListAdapter {
        if (_adapter == null) _adapter = CharacterListAdapter()
        return requireNotNull(_adapter)
    }

    private fun getSearchAdapter(): CharacterListAdapter {
        if (_searchAdapter == null) _searchAdapter = CharacterListAdapter()
        return requireNotNull(_searchAdapter)
    }

    private fun performSearch(query: String) {
        keyword = query
    }
}