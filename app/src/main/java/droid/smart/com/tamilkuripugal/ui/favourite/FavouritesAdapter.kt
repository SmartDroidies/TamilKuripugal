package droid.smart.com.tamilkuripugal.ui.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.FavouriteItemBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.ui.common.DataBoundListAdapter
import droid.smart.com.tamilkuripugal.vo.Favourite

class FavouritesAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((Favourite) -> Unit)?
) : DataBoundListAdapter<Favourite, FavouriteItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Favourite>() {
        override fun areItemsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
            return oldItem.kurippuId == newItem.kurippuId
        }

        override fun areContentsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
            return oldItem.kurippuId == newItem.kurippuId
            //return oldItem.title == newItem.title
        }
    }
) {
    override fun createBinding(parent: ViewGroup): FavouriteItemBinding {
        val binding = DataBindingUtil
            .inflate<FavouriteItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.favourite_item,
                parent,
                false,
                dataBindingComponent
            )
/*
        binding.root.setOnClickListener {
            binding.kurippu?.let {
                callback?.invoke(it)
            }
        }
*/
        return binding
    }

    override fun bind(binding: FavouriteItemBinding, item: Favourite) {
        binding.kurippu = item
    }
}






