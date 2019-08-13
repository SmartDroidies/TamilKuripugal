package droid.smart.com.tamilkuripugal.ui.draft

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.DraftKuripuItemBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.ui.common.DataBoundListAdapter
import droid.smart.com.tamilkuripugal.vo.Kurippu

class DraftKuripugalAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((Kurippu) -> Unit)?
) : DataBoundListAdapter<Kurippu, DraftKuripuItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Kurippu>() {
        override fun areItemsTheSame(oldItem: Kurippu, newItem: Kurippu): Boolean {
            return oldItem.kurippuId == newItem.kurippuId
        }

        override fun areContentsTheSame(oldItem: Kurippu, newItem: Kurippu): Boolean {
            return oldItem.title == newItem.title
        }
    }
) {
    override fun createBinding(parent: ViewGroup): DraftKuripuItemBinding {
        val binding = DataBindingUtil
            .inflate<DraftKuripuItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.draft_kuripu_item,
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

    override fun bind(binding: DraftKuripuItemBinding, item: Kurippu) {
        binding.kurippu = item
    }
}

