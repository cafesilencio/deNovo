# deNovo

WIP

This project was a thought experiment in reducing the boilerplate around creating Recycle View adapters in Android. I found myself subclassing the same Recycle View adapter class over and over again.
I wondered if I could create a DSL that would reduce the boiler plate and abstract the Recycle View adapter to a declaration. 

The DSL requires passing in the implementation for the onBindViewHolder() and onCreateViewHolder() methods as well as optional passing of functions for getItemViewType() and identity functions for swapping the adapter contents.
In addition item clicks and long presses are supported as well as clicks on specified view Ids.  

Usage:

Define a view holder:

class MyViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView)



Define the onBindViewHolder function as an expression: 

val myOnBindViewHolderFun: (holder: MyViewHolder, position: Int, element: MyDataType) -> Unit = { holder, position, element ->
    holder.itemType.viewComponent1.text = element.someString
    holder.itemType.viewComponent2.text = element.anotherString
}



Define a view holder factory expression:

val viewHolderFactory: (parent: ViewGroup, viewType: Int) -> MyViewHolder = { parent, viewType ->
    val v = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
    MyViewHolder(v)
}



Optionally define the getItemViewType() as an expression

val myItemViewTypeFun: DeNovoGetItemViewTypeFun = { R.layout.my_view_layout }



Optionally define an expression to determine if the data element the adapter operates on  have the same id.
Internally the adapter implementation will use this expression to efficiently update the adapter contents using DiffUtil from the Android support library.

val mySameIdFun: DeNovoSameItemFun<MyDataType> = { item1, item2 -> item1.id == item2.id }

also available is a same contents expression

val mySameContentsFun: DeNovoSameItemFun<MyDataType> = { item1, item2 -> item1.id == item2.id  && item1.someOtherThing == item2.someOtherThing }



Optionally define one or more item selected expressions for single click or long press events.

The click and long press definition is: DeNovoItemSelectedFun<T> = (Pair<Int, T>) -> Unit

val clickFun: DeNovoItemSelectedFun<T> = { data ->
    val position: Int = data.first //this is the position of the element in the list
    val element: MyDataType = data.second //this is the data at the position that was clicked/long pressed
}



This expression type can be used for clicks on any view element in the layout by passing the resource Id and an expression similar to the previous example.



An example:

val someLayoutViewIdClickFun: DeNovoViewClickFun<MyDataType> = { //do something with the data when this view element is clicked }

val myAdapter = deNovoAdapter(myOnBindViewHolderFun, viewHolderFactory) {

    itemTypeFun = myItemViewTypeFun                                             //optional but if you don't define this be sure your viewHolderFactory expression inflates the right layout
    
    sameIdFun = mySameIdFun                                                     //optional
    
    singleClickDelegate = { //do something with the data }                      //optional
    
    longPressDelegate = { //do something with the data }                        //optional
    
    auxViewClickMap = mapOf(Pair(R.id.someViewId, someLayoutViewIdClickFun))    //optional
    
}