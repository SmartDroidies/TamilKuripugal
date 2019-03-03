package droid.smart.com.tamilkuripugal.ui.newkuripugal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.NewKuripuItemBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.ui.common.DataBoundListAdapter
import droid.smart.com.tamilkuripugal.vo.Kurippu

class NewKuripugalAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((Kurippu) -> Unit)?
) : DataBoundListAdapter<Kurippu, NewKuripuItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Kurippu>() {
        override fun areItemsTheSame(oldItem: Kurippu, newItem: Kurippu): Boolean {
            return oldItem.kurippuId == newItem.kurippuId
        }

        override fun areContentsTheSame(oldItem: Kurippu, newItem: Kurippu): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.categoryObj == newItem.categoryObj
                    && oldItem.image == newItem.image
        }
    }
) {
    override fun createBinding(parent: ViewGroup): NewKuripuItemBinding {
        val binding = DataBindingUtil
            .inflate<NewKuripuItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.new_kuripu_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.setOnClickListener {
            binding.kurippu?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: NewKuripuItemBinding, item: Kurippu) {
        binding.kurippu = item
    }
}

