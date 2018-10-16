package net.cafesilencio.denovo

/**
 * Created on 10/15/18.
 */
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

typealias DeNovoSameIdFunc<T> = (T, T) -> Boolean
typealias DeNovoHaveSameContentFunc<T> = (T, T) -> Boolean
typealias DeNovoGetItemViewTypeFunc = (Int) -> Int

data class DeNovoAdapter<T, U : RecyclerView.ViewHolder>(val onBindViewHolder: (holder: U, position: Int, element: T) -> Unit,
                                                         val viewHolderFactory: (parent: ViewGroup, viewType: Int) -> U,
                                                         val sameIdFunc: DeNovoSameIdFunc<T>?,
                                                         val sameContentFunc: DeNovoHaveSameContentFunc<T>?,
                                                         val getItemTypeFunc: DeNovoGetItemViewTypeFunc?): RecyclerView.Adapter<U>() {

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
    override fun getItemViewType(position: Int): Int = getItemTypeFunc?.invoke(position) ?: super.getItemViewType(position)

    fun getClicks(): Observable<Pair<Int, T>> = clicksRelay.hide()
    fun getLongClicks(): Observable<Pair<Int, T>> = longClicksRelay.hide()
    fun swap(newValues: List<T>) {
        val diffResult = DiffUtil.calculateDiff(createElementsDiffCallback(newValues))
        values.clear()
        values.addAll(newValues)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun createElementsDiffCallback(newValues: List<T>): DiffUtil.Callback {
        return object: DiffUtil.Callback() {
            override fun getOldListSize(): Int = values.size
            override fun getNewListSize(): Int = newValues.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = sameIdFunc?.invoke(values[oldItemPosition], newValues[newItemPosition]) ?: true
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = sameContentFunc?.invoke(values[oldItemPosition], newValues[newItemPosition]) ?: false
        }
    }
}