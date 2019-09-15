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
import droid.smart.com.tamilkuripugal.vo.FavouriteKurippu

class FavouritesAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((FavouriteKurippu) -> Unit)?
) : DataBoundListAdapter<FavouriteKurippu, FavouriteItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<FavouriteKurippu>() {
        override fun areItemsTheSame(oldItem: FavouriteKurippu, newItem: FavouriteKurippu): Boolean {
            return oldItem.kurippuId == newItem.kurippuId
        }

        override fun areContentsTheSame(oldItem: FavouriteKurippu, newItem: FavouriteKurippu): Boolean {
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
        binding.root.setOnClickListener {
            binding.favourite?.let {
                callback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: FavouriteItemBinding, item: FavouriteKurippu) {
        binding.favourite = item
    }
}






