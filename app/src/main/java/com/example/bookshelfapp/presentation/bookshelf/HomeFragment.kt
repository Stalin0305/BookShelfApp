package com.example.bookshelfapp.presentation.bookshelf

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookshelfapp.R
import com.example.bookshelfapp.data.utils.BooksOrder
import com.example.bookshelfapp.data.utils.OrderType
import com.example.bookshelfapp.databinding.FragmentHomeBinding
import com.example.bookshelfapp.presentation.auth.AuthViewModel
import com.example.bookshelfapp.presentation.bookshelf.adapters.BookListAdapter
import com.example.bookshelfapp.presentation.bookshelf.placeholders.AddFavouriteUIState
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookItem
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookListUiState
import com.example.bookshelfapp.presentation.bookshelf.placeholders.RemoveFavouriteUIState
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding: FragmentHomeBinding
        get() = _binding!!

    private val authViewModel by viewModels<AuthViewModel>()
    private val bookShelfViewModel by viewModels<BookShelfViewModel>()

    private val args: HomeFragmentArgs by navArgs()

    private var currentSortOrderType: OrderType = OrderType.Ascending

    private var currentSortType: BooksOrder = BooksOrder.Title(currentSortOrderType)

    private var bookListAdapter: BookListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bookShelfViewModel.currentUserInfo = args.currentUserInfo
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        clickListeners()
        setObservers()
    }

    private fun initializeViews() {
        bookShelfViewModel.fetchBookListAndFavourites(currentSortType)
        binding.sortChipGroup.check(R.id.titleChip)
        binding.switchAscendingOrder.isChecked = true

        bookListAdapter = BookListAdapter(
            bookItemList = bookShelfViewModel.finalBookItemList,
            onBookItemClicked = {},
            onFavouriteIconClicked = { item, isChecked, position ->
                if (isChecked) {
                    bookShelfViewModel.addToFavourites(item, position)
                } else {
                    bookShelfViewModel.removeFromFavourites(item, position)
                }
            }
        )
        binding.rvBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBooks.adapter = bookListAdapter
    }

    private fun clickListeners() {
        binding.toolbar.logOutButton.setOnClickListener {
            authViewModel.logOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.sortChipGroup.setOnCheckedStateChangeListener { chipGroup, checkedId ->
            if (checkedId.isNotEmpty()) {
                val chipName = chipGroup.findViewById<Chip>(checkedId[0]).text
                when (chipName.toString()) {
                    getString(R.string.hits_chip) -> {
                        Log.d("HomeFragment", chipName.toString())
                        currentSortType = BooksOrder.Hits(currentSortOrderType)
                        refreshSortOrder()

                    }

                    getString(R.string.title_chip) -> {
                        Log.d("HomeFragment", chipName.toString())
                        currentSortType = BooksOrder.Title(currentSortOrderType)
                        refreshSortOrder()

                    }

                    getString(R.string.favs_chip) -> {
                        currentSortType = BooksOrder.Favs(currentSortOrderType)
                        refreshSortOrder()
                    }
                }
            }
        }

        binding.switchAscendingOrder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("HomeFragment", "Switch is checked")
                currentSortOrderType = OrderType.Ascending
                currentSortType.orderType = currentSortOrderType
                refreshSortOrder()
            } else {
                currentSortOrderType = OrderType.Descending
                currentSortType.orderType = currentSortOrderType
                refreshSortOrder()
            }
        }

    }

    private fun refreshSortOrder() {
        val sortedList = bookShelfViewModel.sortBookListBasedOnOrder(currentSortType)
        bookListAdapter?.updateList(sortedList)
        bookListAdapter?.notifyDataSetChanged()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            bookShelfViewModel.bookListFlow.collectLatest { bookListUiState ->
                when (bookListUiState) {
                    is BookListUiState.BookListUILoadingState -> {
                        binding.rvBooks.isVisible = false
                        binding.layoutError.clBookListError.isVisible = false
                        showProgressBar()
                    }

                    is BookListUiState.BookListUISuccessState -> {
                        setBookListUI(bookListUiState.bookList)
                    }

                    is BookListUiState.BookListUIErrorState -> {
                        setBookListErrorUI()
                    }

                    null -> {}
                }
            }

            bookShelfViewModel.addFavouriteFlow.collectLatest { addFavouriteUIState ->
                when (addFavouriteUIState) {
                    is AddFavouriteUIState.AddFavouriteErrorState -> {
                        Toast.makeText(
                            requireContext(),
                            "Failed to add favourite",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is AddFavouriteUIState.AddFavouriteSuccessState -> {
                        refreshSortOrder()
                    }

                    null -> {}
                }
            }

            bookShelfViewModel.removeFavouriteFlow.collectLatest { removeFavouriteUIState ->
                when (removeFavouriteUIState) {
                    is RemoveFavouriteUIState.RemoveFavouriteErrorState -> {
                        Toast.makeText(
                            requireContext(),
                            "Failed to add favourite",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is RemoveFavouriteUIState.RemoveFavouriteSuccessState -> {
                        refreshSortOrder()
                    }

                    null -> {}
                }
            }
        }

    }

    private fun setBookListUI(bookList: List<BookItem>) {
        hideProgressBar()
        binding.layoutError.clBookListError.isVisible = false
        binding.rvBooks.isVisible = true
        bookListAdapter?.updateList(bookList)
        bookListAdapter?.notifyDataSetChanged()

    }

    private fun setBookListErrorUI() {
        binding.rvBooks.isVisible = false
        hideProgressBar()
        binding.layoutError.clBookListError.isVisible = true
        binding.layoutError.btnTryAgain.setOnClickListener {
            bookShelfViewModel.fetchBookListAndFavourites(currentSortType)
        }
    }

    private fun showProgressBar() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgressBar() = run { binding.progressBar.isVisible = false }


}