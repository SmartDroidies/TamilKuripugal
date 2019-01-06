package droid.smart.com.tamilkuripugal.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.snackbar.Snackbar
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.CategoryItemBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.vo.Category

/**
 * A RecyclerView adapter for [Category] class.
 */

class CategoryListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val repoClickCallback: ((Category) -> Unit)?
) : DataBoundListAdapter<Category, CategoryItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.category == newItem.category
                    && oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.description == newItem.description
        }

    }
) {
    override fun bind(binding: CategoryItemBinding, item: Category) {
        binding.category = item
    }

    override fun createBinding(parent: ViewGroup): CategoryItemBinding {
        val binding = DataBindingUtil.inflate<CategoryItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.category_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            Snackbar.make(it, "Get into the category", Snackbar.LENGTH_LONG).show()
            binding.category?.let {
                repoClickCallback?.invoke(it)
            }
        }
        return binding
    }

}

