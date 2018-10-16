package net.cafesilencio.denovo

/**
 * Created on 10/15/18.
 */
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

typealias DeNovoSameIdFun<T> = (T, T) -> Boolean
typealias DeNovoHaveSameContentFun<T> = (T, T) -> Boolean
typealias DeNovoGetItemViewTypeFun = (Int) -> Int

data class DeNovoAdapter<T, U : RecyclerView.ViewHolder>(val onBindViewHolder: (holder: U, position: Int, element: T) -> Unit,
                                                         val viewHolderFactory: (parent: ViewGroup, viewType: Int) -> U,
                                                         val sameIdFun: DeNovoSameIdFun<T>?,
                                                         val sameContentFun: DeNovoHaveSameContentFun<T>?,
                                                         val itemTypeFun: DeNovoGetItemViewTypeFun?): RecyclerView.Adapter<U>() {

    private val values: MutableList<T> = mutableListOf()
    private val clicksRelay: PublishRelay<Pair<Int, T>> = PublishRelay.create()
    private val longClicksRelay: PublishRelay<Pair<Int, T>> = PublishRelay.create()

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: U, position: Int) {
        val element: T = values[position]
        holder.itemView.setOnClickListener { clicksRelay.accept(Pair(position, element)) }
        holder.itemView.setOnLongClickListener {
            longClicksRelay.accept(Pair(position, element))
            true
        }
        onBindViewHolder.invoke(holder, position, element)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): U = viewHolderFactory(parent, viewType)
    override fun getItemViewType(position: Int): Int = itemTypeFun?.invoke(position) ?: super.getItemViewType(position)

    fun getCloneOfValues(): List<T> = values.toList()
    fun getClicks(): Observable<Pair<Int, T>> = clicksRelay.hide()
    fun getLongClicks(): Observable<Pair<Int, T>> = longClicksRelay.hide()
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
}