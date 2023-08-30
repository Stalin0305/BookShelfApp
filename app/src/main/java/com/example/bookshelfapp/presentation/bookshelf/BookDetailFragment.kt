package com.example.bookshelfapp.presentation.bookshelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.bookshelfapp.R
import com.example.bookshelfapp.databinding.FragmentBookDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val bookShelfViewModel by viewModels<BookShelfViewModel>()

    private val args: BookDetailFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bookShelfViewModel.bookDetailItem = args.bookItem
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        clickListeners()

    }

    private fun clickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun initializeViews() {
        val bookItem = bookShelfViewModel.bookDetailItem
        binding.tvBookTitle.text = bookItem?.title
        binding.tvBookHits.text = String.format(getString(R.string.number_of_hits), bookItem?.hits)
        binding.tvAlias.text = String.format(getString(R.string.alias), bookItem?.alias)
        binding.tvUpdatedOn.text = String.format(getString(R.string.updated_on), convertTimeToDate(bookItem?.lastChapterDate))

        Glide.with(requireContext())
            .load(bookItem?.image)
            .error(R.drawable.bookshelf)
            .placeholder(R.drawable.bookshelf)
            .into(binding.ivBookIcon)
    }

    private fun convertTimeToDate(value: Int?): String {
        value?.let {
            val date = Date(value.toLong() * 1000)
            val dateFormat = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
            return dateFormat.format(date)
        }
        return "Invalid date format"

    }
}