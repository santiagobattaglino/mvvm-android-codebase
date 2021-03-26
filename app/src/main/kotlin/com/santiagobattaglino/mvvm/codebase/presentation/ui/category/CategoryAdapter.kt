package com.santiagobattaglino.mvvm.codebase.presentation.ui.category

import com.santiagobattaglino.mvvm.codebase.presentation.ui.AutoUpdatableAdapter
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.santiagobattaglino.mvvm.codebase.R
import com.santiagobattaglino.mvvm.codebase.domain.entity.Category
import kotlinx.android.synthetic.main.item_category_product.view.*
import kotlin.properties.Delegates

class CategoryAdapter(
    private val onViewHolderClick: OnViewHolderClick?
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>(), AutoUpdatableAdapter {

    companion object {
        const val VIEW_TYPE_NORMAL = 0
    }

    var mData: List<Category> by Delegates.observable(emptyList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            createView(parent.context, parent, viewType),
            onViewHolderClick
        )
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_NORMAL
    }

    override fun getItemCount() = mData.size

    private fun getItem(index: Int): Category {
        return mData[index]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    private fun createView(context: Context, viewGroup: ViewGroup, viewType: Int): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.item_category_product, viewGroup, false)
    }

    interface OnViewHolderClick {
        fun dataViewClickFromList(view: View, position: Int, data: Category)
    }

    inner class ViewHolder(
        itemView: View,
        viewHolderClickListener: OnViewHolderClick?
    ) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            if (viewHolderClickListener != null)
                itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (adapterPosition >= 0) {
                onViewHolderClick?.dataViewClickFromList(
                    view,
                    adapterPosition,
                    getItem(adapterPosition)
                )
            }
        }

        fun bind(category: Category) = with(itemView) {
            when (itemViewType) {
                VIEW_TYPE_NORMAL -> {
                    if (category.isSelected) {
                        category_container.background =
                            ContextCompat.getDrawable(context, R.drawable.fill_cat_icon_selected)
                    } else {
                        category_container.background =
                            ContextCompat.getDrawable(context, R.drawable.fill_cat_icon)
                    }
                    /*try {
                        image.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                getResourceId(
                                    "ic_cat_${category.id}",
                                    "drawable",
                                    context.packageName,
                                    resources
                                ),
                                null
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }*/
                    title.text = category.name
                }
            }
        }

        private fun getResourceId(
            pVariableName: String?,
            pResourcename: String?,
            pPackageName: String?,
            res: Resources
        ): Int {
            return try {
                res.getIdentifier(pVariableName, pResourcename, pPackageName)
            } catch (e: Exception) {
                e.printStackTrace()
                -1
            }
        }
    }
}