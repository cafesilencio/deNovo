package net.cafesilencio.denovo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created on 10/15/18.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class DeNovoAdapterTest {


    @Test
    fun onBindViewHolderIsCalledTest() {
        var indicator = false

        val myAdapter = deNovoAdapter( { holder: MyViewHolder, position: Int, element: String ->
            indicator = true
        }, {_: ViewGroup, _: Int -> MyViewHolder(View(RuntimeEnvironment.application))} ) {}.also {
            it.swap(listOf("foo", "bar"))
        }

        myAdapter.onBindViewHolder(MyViewHolder(View(RuntimeEnvironment.application)), 0)

        assertTrue(indicator)
    }

    @Test
    fun viewHolderFactoryIsCalledTest() {
        val myAdapter = deNovoAdapter( { _: MyViewHolder, _: Int, _: String -> },
                { parent: ViewGroup, _ : Int -> MyViewHolder(View(RuntimeEnvironment.application))
                    MyViewHolder(View(parent.context))
                } ) {}.also { it.swap(listOf("foo", "bar")) }

        val vh = myAdapter.viewHolderFactory.invoke(LinearLayout(RuntimeEnvironment.application), 0)

        assertNotNull(vh)
    }

    @Test
    fun hasSameIdFun() {
        val myAdapter = deNovoAdapter( { holder: MyViewHolder, position: Int, element: String -> },
                {_: ViewGroup, _: Int -> MyViewHolder(View(RuntimeEnvironment.application))} ) {
            sameIdFunc = { item1, item2 ->
               item1 == item2
            }
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        val result = myAdapter.sameIdFunc?.invoke("foobar", "foobar") ?: false
        assertTrue(result)
    }

    @Test
    fun hasSameContentFun() {
        val myAdapter = deNovoAdapter( { holder: MyViewHolder, position: Int, element: String -> },
                {_: ViewGroup, _: Int -> MyViewHolder(View(RuntimeEnvironment.application))} ) {
            sameContentFunc = { item1, item2 ->
                item1 == item2
            }
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        val result = myAdapter.sameContentFunc?.invoke("foobar", "foobar") ?: false
        assertTrue(result)
    }

    @Test
    fun getItemTypeFunc() {
        val myAdapter = deNovoAdapter( { holder: MyViewHolder, position: Int, element: String -> },
                {_: ViewGroup, _: Int -> MyViewHolder(View(RuntimeEnvironment.application))} ) {
            getItemTypeFunc = {
                it
            }
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        val result = myAdapter.getItemTypeFunc?.invoke(2046)

        assertThat(result, `is`(2046))
    }

    @Test
    fun getClicks() {
        var clickItem: Pair<Int, String>? = null

        val myAdapter = deNovoAdapter( { _: MyViewHolder, _: Int, _: String -> },
                {_: ViewGroup, _: Int -> MyViewHolder(View(RuntimeEnvironment.application))} ) {
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        myAdapter.getClicks().subscribe {
            clickItem = it
        }

        val vh = MyViewHolder(View(RuntimeEnvironment.application))
        myAdapter.onBindViewHolder(vh, 1)
        vh.itemView.performClick()

        assertThat(clickItem?.second, `is`("bar"))
    }

    @Test
    fun getLongClicks() {
        var clickItem: Pair<Int, String>? = null

        val myAdapter = deNovoAdapter( { _: MyViewHolder, _: Int, _: String -> },
                {_: ViewGroup, _: Int -> MyViewHolder(View(RuntimeEnvironment.application))} ) {
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        myAdapter.getLongClicks().subscribe {
            clickItem = it
        }

        val vh = MyViewHolder(View(RuntimeEnvironment.application))
        myAdapter.onBindViewHolder(vh, 1)
        vh.itemView.performLongClick()

        assertThat(clickItem?.second, `is`("bar"))
    }


    class MyViewHolder(v: View): RecyclerView.ViewHolder(v)
}