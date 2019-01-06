package droid.smart.com.tamilkuripugal.ui.common

import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.recyclerview.widget.DiffUtil
import com.smart.droid.tamil.tips.databinding.CategoryItemBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.vo.Category

/**
 * A RecyclerView adapter for [Category] class.
 */

class CategoryListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val showFullName: Boolean,
    private val repoClickCallback: ((Category) -> Unit)?
) : DataBoundListAdapter<Category, CategoryItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
) {
    override fun bind(binding: CategoryItemBinding, item: Category) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createBinding(parent: ViewGroup): CategoryItemBinding {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

