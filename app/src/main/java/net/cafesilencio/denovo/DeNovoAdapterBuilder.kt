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
    var sameIdFun: DeNovoSameItemFun<T>? = null
    var sameContentFun: DeNovoSameItemFun<T>? = null
    var itemTypeFun: DeNovoGetItemViewTypeFun? = null
    var itemSelectedDelegate: DeNovoItemSelectedFun<T>? = null
    var longPressDelegate: DeNovoItemSelectedFun<T>? = null
    var auxViewClickDelegates: Map<Int, DeNovoItemSelectedFun<T>> = mapOf()

    fun build(): DeNovoAdapter<T, U > = DeNovoAdapter(onBindViewHolder, viewHolderFactory, itemSelectedDelegate, longPressDelegate, sameIdFun, sameContentFun, itemTypeFun, auxViewClickDelegates)
}