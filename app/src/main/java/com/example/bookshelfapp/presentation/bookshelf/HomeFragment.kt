package com.example.bookshelfapp.presentation.bookshelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookItem
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookListUiState
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

        bookShelfViewModel.fetchBookListAndFavourites(BooksOrder.Title(OrderType.Ascending))

        binding.toolbar.logOutButton.setOnClickListener {
            authViewModel.logOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        setObservers()
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
        }

    }

    private fun setBookListUI(bookList: List<BookItem>) {
        hideProgressBar()
        binding.layoutError.clBookListError.isVisible = false
        binding.rvBooks.isVisible = true
        val adapter = BookListAdapter(
            bookItemList = bookList as MutableList<BookItem>,
            onBookItemClicked =  {},
            onFavouriteIconClicked = {}
        )
        binding.rvBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBooks.adapter = adapter

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