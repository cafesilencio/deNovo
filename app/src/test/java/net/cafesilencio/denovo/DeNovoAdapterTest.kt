package net.cafesilencio.denovo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val vh = MyViewHolder(View(RuntimeEnvironment.application))

        val myAdapter = deNovoAdapter( { holder: MyViewHolder, position: Int, element: String ->
            indicator = true
        }, { _: ViewGroup, _: Int -> vh } ) {}.also {
            it.swap(listOf("foo", "bar"))
        }

        myAdapter.onBindViewHolder(vh, 0)

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
            sameIdFun = { item1, item2 ->
               item1 == item2
            }
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        val result = myAdapter.sameIdFun?.invoke("foobar", "foobar") ?: false
        assertTrue(result)
    }

    @Test
    fun hasSameContentFun() {
        val myAdapter = deNovoAdapter( { holder: MyViewHolder, position: Int, element: String -> },
                {_: ViewGroup, _: Int -> MyViewHolder(View(RuntimeEnvironment.application))} ) {
            sameContentFun = { item1, item2 ->
                item1 == item2
            }
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        val result = myAdapter.sameContentFun?.invoke("foobar", "foobar") ?: false
        assertTrue(result)
    }

    @Test
    fun getItemTypeFunc() {
        val myAdapter = deNovoAdapter( { holder: MyViewHolder, position: Int, element: String -> },
                {_: ViewGroup, _: Int -> MyViewHolder(View(RuntimeEnvironment.application))} ) {
            itemTypeFun = {
                it
            }
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        val result = myAdapter.itemTypeFun?.invoke(2046)

        assertThat(result, `is`(2046))
    }

    @Test
    fun clicksTest() {
        var clickItem: Pair<Int, String>? = null
        val clickDelegate: DeNovoItemSelectedFun<String> = {
            clickItem = it
        }
        val vh = MyViewHolder(View(RuntimeEnvironment.application))

        val myAdapter = deNovoAdapter(
                { _: MyViewHolder, _: Int, _: String -> },
                { _: ViewGroup, _: Int -> vh }) {
            itemSelectedDelegate = clickDelegate
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        myAdapter.onBindViewHolder(vh, 1)
        vh.itemView.performClick()

        assertThat(clickItem?.second, `is`("bar"))
    }

    @Test
    fun longPressTest() {
        var clickItem: Pair<Int, String>? = null
        val longPressFun: DeNovoItemSelectedFun<String> = {
            clickItem = it
        }
        val vh = MyViewHolder(View(RuntimeEnvironment.application))

        val myAdapter = deNovoAdapter(
                { _: MyViewHolder, _: Int, _: String -> },
                { _: ViewGroup, _: Int -> vh }) {
            longPressDelegate = longPressFun
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        myAdapter.onBindViewHolder(vh, 1)
        vh.itemView.performLongClick()

        assertThat(clickItem?.second, `is`("bar"))
    }

    @Test
    fun auxViewClickTest() {
        val buttonViewId = 8675309
        var clickItem: Pair<Int, String>? = null
        val auxViewClickFun: DeNovoItemSelectedFun<String> = { clickItem = it }

        val button = Button(RuntimeEnvironment.application)
        button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        button.id = buttonViewId

        val rootView = LinearLayout(RuntimeEnvironment.application)
        rootView.addView(button)

        val viewClickMap = mapOf(Pair(buttonViewId, auxViewClickFun))

        val vh = MyViewHolder(rootView)

        val myAdapter = deNovoAdapter(
                { _: MyViewHolder, _: Int, _: String -> },
                { _: ViewGroup, _: Int -> vh }) {
            auxViewClickDelegates = viewClickMap
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        myAdapter.onBindViewHolder(vh, 1)
        vh.itemView.findViewById<Button>(buttonViewId).performClick()

        assertThat(clickItem?.second, `is`("bar"))
    }

    @Test
    fun auxViewClickTest_whenResourceIdNotPresent() {
        val buttonViewId = 8675309
        var clickItem: Pair<Int, String>? = null
        val auxViewClickFun: DeNovoItemSelectedFun<String> = { clickItem = it }

        val button = Button(RuntimeEnvironment.application)
        button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        button.id = buttonViewId

        val rootView = LinearLayout(RuntimeEnvironment.application)
        rootView.addView(button)

        val viewClickMap = mapOf(Pair(1234567, auxViewClickFun))

        val vh = MyViewHolder(rootView)

        val myAdapter = deNovoAdapter(
                { _: MyViewHolder, _: Int, _: String -> },
                { _: ViewGroup, _: Int -> vh }) {
            auxViewClickDelegates = viewClickMap
        }.also {
            it.swap(listOf("foo", "bar"))
        }

        myAdapter.onBindViewHolder(vh, 1)
        vh.itemView.findViewById<Button>(buttonViewId).performClick()

        //the real test is that there is no null pointer exception from the findViewById in the adapter not blowing up
        assertNull(clickItem)
    }

    @Test
    fun cloneValuesTest() {
        val vh = MyViewHolder(View(RuntimeEnvironment.application))
        val itemList = listOf("foo", "bar")

        val myAdapter = deNovoAdapter( { _: MyViewHolder, _: Int, _: String ->
        }, { _: ViewGroup, _: Int -> vh } ) {}.also {
            it.swap(itemList)
        }

        val result: List<String> = myAdapter.getCloneOfValues()

        assertNotSame(result, itemList)
        assertThat(result.size, `is`(2))
        assertThat(result[0], `is`("foo"))
        assertThat(result[1], `is`("bar"))
    }

    class MyViewHolder(v: View): RecyclerView.ViewHolder(v)
}