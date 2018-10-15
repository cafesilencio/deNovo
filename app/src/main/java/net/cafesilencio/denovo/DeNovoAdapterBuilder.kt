package net.cafesilencio.denovo

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created on 10/15/18.
 */
@DslMarker
annotation class DeNovoAdapterDsl

fun <T, U : RecyclerView.ViewHolder> deNovoAdapter(onBindViewHolder: (holder: U, position: Int, element: T) -> Unit, viewHolderFactory: (parent: ViewGroup, viewType: Int) -> U, block: DeNovoAdapterBuilder<T, U>.() -> Unit): DeNovoAdapter<T, U> = DeNovoAdapterBuilder(onBindViewHolder, viewHolderFactory).apply(block).build()

@DeNovoAdapterDsl
class DeNovoAdapterBuilder<T, U : RecyclerView.ViewHolder> constructor(private val onBindViewHolder: (holder: U, position: Int, element: T) -> Unit,
                                                                       private val viewHolderFactory: (parent: ViewGroup, viewType: Int) -> U) {
    var sameIdFunc: DeNovoSameIdFunc<T>? = null
    var sameContentFunc: DeNovoHaveSameContentFunc<T>? = null
    var getItemTypeFunc: DeNovoGetItemViewTypeFunc? = null

    fun build(): DeNovoAdapter<T, U > = DeNovoAdapter(onBindViewHolder, viewHolderFactory, sameIdFunc, sameContentFunc, getItemTypeFunc)
}