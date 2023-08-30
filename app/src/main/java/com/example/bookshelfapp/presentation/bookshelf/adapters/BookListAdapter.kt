package com.example.bookshelfapp.presentation.bookshelf.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookshelfapp.R
import com.example.bookshelfapp.databinding.LayoutBookItemBinding
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookItem

class BookListAdapter(
    private var bookItemList: List<BookItem>,
    var onFavouriteIconClicked: (BookItem, Boolean, Int) -> Unit,
    var onBookItemClicked: (BookItem) -> Unit
) : RecyclerView.Adapter<BookItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        val binding =
            LayoutBookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookItemViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int {
        return bookItemList.size
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        val item = bookItemList[position] as BookItem
        holder.bind(
            position,
            item,
            onFavouriteIconClicked = { bookItem, isChecked, position ->
                onFavouriteIconClicked(bookItem, isChecked, position)
            },
            onBookItemClicked = { bookItem ->
                onBookItemClicked(bookItem)
            }
        )
    }

    fun updateList(list: List<BookItem>) {
        bookItemList = list
    }


}

class BookItemViewHolder(val binding: LayoutBookItemBinding, val context: Context) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        position: Int,
        item: BookItem,
        onFavouriteIconClicked: (BookItem, Boolean, Int) -> Unit,
        onBookItemClicked: (BookItem) -> Unit
    ) {
        with(binding) {
            var isChecked = item.isFavourite
            //Get Primary color of the theme and set it based on the isFavourite flag
            // Create a TypedValue instance to store the resolved color
            val typedValue = TypedValue()
            // Obtain the primary color from the current theme
            context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
            // Get the resolved color value
            val primaryColor = typedValue.data

            //Set the book details
            tvBookTitle.text = item.title
            tvHits.text = String.format(context.getString(R.string.number_of_hits), item.hits)
            if (isChecked) {
                btnFavourite.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN)
            } else {
                btnFavourite.clearColorFilter()
            }

            btnFavourite.setOnClickListener {
                isChecked = !isChecked
                onFavouriteIconClicked(item, isChecked, position)
                if (isChecked) {
                    btnFavourite.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN)
                } else {
                    btnFavourite.clearColorFilter()
                }
            }

            clBookItem.setOnClickListener {
                onBookItemClicked(item)
            }

            //Set Book icon
            Glide.with(context)
                .load(item.image)
                .error(R.drawable.bookshelf)
                .placeholder(R.drawable.bookshelf)
                .into(ivBookIcon)
        }

    }
}