package net.cafesilencio.denovo

/**
 * Created on 10/15/18.
 */
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

typealias DeNovoSameItemFun<T> = (T, T) -> Boolean
typealias DeNovoGetItemViewTypeFun = (Int) -> Int
typealias DeNovoItemSelectedFun<T> = (Pair<Int, T>) -> Unit


data class DeNovoAdapter<T, U : RecyclerView.ViewHolder>(val onBindViewHolder: (holder: U, position: Int, element: T) -> Unit,
                                                         val viewHolderFactory: (parent: ViewGroup, viewType: Int) -> U,
                                                         val itemSelectedDelegate: DeNovoItemSelectedFun<T>?,
                                                         val longPressDelegate: DeNovoItemSelectedFun<T>?,
                                                         val sameIdFun: DeNovoSameItemFun<T>?,
                                                         val sameContentFun: DeNovoSameItemFun<T>?,
                                                         val itemTypeFun: DeNovoGetItemViewTypeFun?,
                                                         val auxViewClickMap: Map<Int, DeNovoItemSelectedFun<T>>): RecyclerView.Adapter<U>() {

    private val values: MutableList<T> = mutableListOf()


    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: U, position: Int) {
        val element: T = values[position]
        itemSelectedDelegate?.let { holder.itemView.setOnClickListener { itemSelectedDelegate.invoke(Pair(position, element)) } }
        longPressDelegate?.let { holder.itemView.setOnLongClickListener {
            longPressDelegate.invoke(Pair(position, element))
            true
        } }
        auxViewClickMap.forEach { entry ->
            holder.itemView.findViewById<View>(entry.key)?.setOnClickListener { entry.value.invoke(Pair(position, element)) }
        }

        onBindViewHolder.invoke(holder, position, element)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): U = viewHolderFactory(parent, viewType)
    override fun getItemViewType(position: Int): Int = itemTypeFun?.invoke(position) ?: super.getItemViewType(position)

    fun getCloneOfValues(): List<T> = values.toList()
    fun swap(newValues: List<T>) {
        DiffUtil.calculateDiff(createElementsDiffCallback(newValues)).dispatchUpdatesTo(this)
        values.clear()
        values.addAll(newValues)
    }

    private fun createElementsDiffCallback(newValues: List<T>): DiffUtil.Callback {
        return object: DiffUtil.Callback() {
            override fun getOldListSize(): Int = values.size
            override fun getNewListSize(): Int = newValues.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = sameIdFun?.invoke(values[oldItemPosition], newValues[newItemPosition]) ?: false
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = sameContentFun?.invoke(values[oldItemPosition], newValues[newItemPosition]) ?: false
        }
    }

    fun getItemAtPosition(position: Int): T? = values.firstOrNull { it == values[position] }
}