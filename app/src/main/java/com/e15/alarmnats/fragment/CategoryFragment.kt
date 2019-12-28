package com.e15.alarmnats.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.*
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.EventsAdapter
import com.e15.alarmnats.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.multilevelview.MultiLevelRecyclerView
import com.multilevelview.models.RecyclerViewItem
import java.io.*
import java.lang.Exception
import java.util.ArrayList
import java.util.function.Predicate

//This here (display task) base on (it's category)
class CategoryFragment : Fragment() {

    private lateinit var recyclerView: MultiLevelRecyclerView
    var adapter: EventsAdapter? = null

    private var categories: ArrayList<Category>? = null

    //All items and (each item) has title orther than (rest of items)
    var allItems: ArrayList<Event> = ArrayList()

    private var dbHandler: ReminderDatabase? = null

    private var tv_show_message: TextView? = null

    lateinit var firebaseDatabase: FirebaseDatabase

    lateinit var eventDatabase: DatabaseReference

    lateinit var databaseUser: DatabaseReference

    lateinit var groupDatabase: DatabaseReference

    lateinit var lnfilterGroup: RelativeLayout

    lateinit var spnGroup: Spinner

    lateinit var spnMember: Spinner

    lateinit var currentUser: FirebaseUser

    lateinit var queryGetIdUser: Query

    lateinit var groupArrayAdapter: ArrayAdapter<String>

    lateinit var databaseEvents: DatabaseReference

    lateinit var categoryDatabase: DatabaseReference

    lateinit var selectedCurrentMemberHashId: String

    //this (field) is used in (local location of this file)
    var currentHashId: String = ""

    lateinit var progressLoadTask:RelativeLayout

    lateinit var listCurrentUserGroup:ArrayList<Group>

    companion object {

        var selectedGroup:Group?=null

        var selectedIdUser=""

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseDatabase = FirebaseDatabase.getInstance()

        eventDatabase = firebaseDatabase.getReference("Events")

        categoryDatabase = FirebaseDatabase.getInstance().getReference("Category")

        //Nested listener event into returned Thread
        currentUser = FirebaseAuth.getInstance().currentUser!!

        databaseUser = firebaseDatabase.getReference("User")

        databaseEvents = firebaseDatabase.getReference("Events")

        queryGetIdUser = databaseUser.orderByChild("email").equalTo(currentUser!!.email)

        groupDatabase = firebaseDatabase.getReference("Group")

//        progressLoadTask=view!!.findViewById(R.id.progressLoadTask)

        dbHandler = ReminderDatabase(this!!.context!!)

        listCurrentUserGroup= ArrayList()

    }

    //return all item (in the database)
    fun fillAllItems(isEdit: Boolean, currentUserId: String, tempAllItems: ArrayList<Event>): ArrayList<Event> {

        var connectRef = firebaseDatabase.getReference(".info/connected")

        connectRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                var connected = p0.getValue(Boolean::class.java)

                if (connected!!) {

                    //Firstly, creating new empty ArrayList<Item>
                    val allItemsEm = ArrayList<Event>()

                    //Getting category from realtime database
                    categoryDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            var isNullAllitems: Boolean = false

                            //isNullAllitems field is check whether allitem is empty
                            if (tempAllItems != null && tempAllItems.size != 0) {

                                isNullAllitems = true

                            }

                            categories = ArrayList()

                            //This code is used to for getting (all categories)
                            if (p0.hasChildren()) {

                                for (c in p0.children) {

                                    var category = c.getValue(Category::class.java)

                                    categories!!.add(category!!)

                                }

                            }

                            //Filter elements having
                            categories = categories!!.filter { it.hashIdUser.equals(currentUserId) } as ArrayList<Category>

                            //This code execute operation (add childs to allitems list)
                            //Looping for (All categories)
                            for (i in categories!!.indices) {

                                val category = categories!![i]

                                //Build a (CategoryItem) base (Category) (at index)
                                //Note that: This item has (CATEGORY_TYPE)
                                val categoryItem = Event(0, category.title, Category.CATEGORY_TYPE, -2, category.hasIdCategory, isEdit, category.color)

                                categoryItem.hashIdUser=category.hashIdUser

                                //checking block operation
//                                categoryItem.hashIdUser=category.hashIdUser

                                //Add item has CATEGORY_TYPE
                                var loopingForAddingNewTypeEvent = ArrayList<Event>()

                                //This is solution for caculating working time wrong!
                                loopingForAddingNewTypeEvent.add(categoryItem)

                                //Using tempAllItems for (this)
                                //Getting (all items) from database (corresponding category)
                                // (has this cagory) in (the current loop)
                                var events = tempAllItems.filter {
                                    (it.category == category.title)
                                }


                                for (j in events.indices) {

                                    val e = events.get(j)


                                    Log.e("--------doneOrNot", e.isDone.toString())

                                    val eventItem = Event(e.hashId, 0, e.title!!, e.description!!, e.place!!,
                                            e.category!!, e.startTime!!, e.endTime, e.date!!,
                                            e.isShow!!, Event.EVENT_TYPE, e.notify!!, e.repeatMode!!,
                                            e.repeatCount!!, e.repeatType!!, 0, categoryItem.color!!)

                                    eventItem.isDone = e.isDone
                                    eventItem.remainingTime = e.remainingTime
                                    eventItem.levelRecusion = e.levelRecusion
                                    eventItem.parentId = e.parentId
                                    eventItem.hashIdUser=e.hashIdUser
                                    eventItem.addChildren(e.children)

                                    loopingForAddingNewTypeEvent.add(eventItem)

                                }

                                //Add child but displaying base on (levelRecusion) of (the corresponding node)
                                recusiveListEvent(loopingForAddingNewTypeEvent as ArrayList<Event>, 0)

                                var tempEvents = ArrayList<Event>()

                                //All node have recustion==0 --> is added to allItems
                                //Category has (levelRecursion==-2)
                                for (e in loopingForAddingNewTypeEvent) {

                                    if (e.parentId.equals("") || e.levelRecusion == -2) {

                                        tempEvents.add(e)

                                    }

                                }
                                allItemsEm.addAll(tempEvents)

                            }
                            allItems.clear()

                            allItems.addAll(allItemsEm)

                            categories!!.clear()

                            if (isNullAllitems) {

                                Log.e("Size of allitems", allItems.size.toString())

                                if (adapter == null) {

                                    adapter = EventsAdapter(this@CategoryFragment!!.context!!, allItems, this@CategoryFragment, recyclerView)

                                    recyclerView!!.layoutManager = LinearLayoutManager(activity)

                                    recyclerView!!.adapter = adapter

                                    recyclerView.setToggleItemOnClick(false)

                                    recyclerView.setAccordion(false)

                                    recyclerView.openTill(0, 1, 2, 3)

                                }else{
                                    adapter!!.notifyDataSetChanged()
                                }
                            } else {

                                adapter!!.notifyDataSetChanged()

                            }

                        }

                    })

                } else {

                    adapter = EventsAdapter(this@CategoryFragment!!.context!!, allItems, this@CategoryFragment, recyclerView)

                    recyclerView!!.layoutManager = LinearLayoutManager(activity)

                    recyclerView!!.adapter = adapter

                    recyclerView.setToggleItemOnClick(false)

                    recyclerView.setAccordion(false)

                    recyclerView.openTill(0, 1, 2, 3)

                    //Firstly, creating new empty ArrayList<Item>
                    val allItemsEm = ArrayList<Event>()

                    //Then we hold (all categorys) from (the database)
                    categories = dbHandler!!.allCategory

                    //Looping for (All categories)
                    for (i in categories!!.indices) {

                        val category = categories!![i]

                        //Build a (CategoryItem) base (Category) (at index)
                        //Note that: This item has (CATEGORY_TYPE)
                        val categoryItem = Event(0, category.title, Category.CATEGORY_TYPE, -2, category.hasIdCategory, isEdit, category.color)

                        categoryItem.hashIdUser=category.hashIdUser

                        //Add item has CATEGORY_TYPE
                        allItemsEm.add(categoryItem)

                        //Getting (all items) from database (corresponding category)
                        // (has this cagory) in (the current loop)
                        var events = dbHandler!!.getEventsByCategory(category.title)

                        var tempEvents = ArrayList<Event>()

                        for (j in events.indices) {

                            val e = events.get(j)

                            Log.e("--------doneOrNot", e.isDone.toString())

                            val eventItem = Event(e.hashId, 0, e.title!!, e.description!!, e.place!!,
                                    e.category!!, e.startTime!!, e.endTime, e.date!!,
                                    e.isShow!!, Event.EVENT_TYPE, e.notify!!, e.repeatMode!!,
                                    e.repeatCount!!, e.repeatType!!, 0, categoryItem.color!!)

                            eventItem.isDone = e.isDone
                            eventItem.remainingTime = e.remainingTime
                            eventItem.levelRecusion = e.levelRecusion
                            eventItem.parentId = e.parentId
                            eventItem.addChildren(e.children)
                            allItemsEm.add(eventItem)
                        }

                        //Add child but displaying base on (levelRecusion) of (the corresponding node)
                        recusiveListEvent(allItemsEm as ArrayList<Event>, 0)

                        //All node have recustion==0 --> is added to allItems
                        //Category has (levelRecursion==-2)
                        for (e in allItemsEm) {

                            if (e.parentId.equals("") || e.levelRecusion == -2) {

                                tempEvents.add(e)

                            }

                        }

                        allItems.clear()

                        allItems.addAll(tempEvents)

                        adapter!!.notifyDataSetChanged()

                    }

                }

            }

        })


        return allItems
    }

    fun recusiveListEvent(listEvents: ArrayList<Event>, level: Int) {

        for (e in listEvents.indices) {

            if (listEvents.get(e).levelRecusion == level) {

                var listChilds: ArrayList<Event> = ArrayList<Event>()

                for (e1 in listEvents) {

                    //Reach (one node) then reachs (all orther nodes)
                    //If (any node) has (parenrId== node.id) --> Add child
                    if (listEvents.get(e).hashId.equals(e1.parentId) && e1.levelRecusion == level + 1) {

                        recusiveListEvent(listEvents, level + 1)

                        listChilds.add(e1)

                    }

                }

                if (listChilds.size != 0) listEvents.get(e).addChildren(listChilds as List<RecyclerViewItem>?)

            }

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.category_fragment, container, false)

        tv_show_message = v.findViewById(R.id.tv_show_message)

        recyclerView = v.findViewById(R.id.recyclerView)

//        var listItemFake= mutableListOf<Item>()

        //Fake data
//        listItemFake.add(Item(1,"asfsd","sdfsd","hanoi","asdas","22:45","22:45","11/16/2019",true,1,"","","","","#dd3333",1))

//        allItems.get(1).addChildren(listItemFake.toList())

        //Filling (all items) to (the adapter)
        try {
            adapter = EventsAdapter(this!!.context!!, allItems, this, recyclerView)

            recyclerView!!.layoutManager = LinearLayoutManager(activity)

            recyclerView!!.adapter = adapter

            recyclerView.setToggleItemOnClick(false)

            recyclerView.setAccordion(false)

            recyclerView.openTill(0, 1, 2, 3)

        } catch (e: Exception) {

//            activity!!.recreate()

            Toast.makeText(activity!!.applicationContext, "Danh sách trống", Toast.LENGTH_SHORT).show()

        }

        //show create event message
        showCreateEventMessage()

        lnfilterGroup = v.findViewById(R.id.lnfilterGroup)

//        lnfilterGroup.isVisible=false
//
//        lnfilterGroup.isClickable=false

        spnGroup = v.findViewById(R.id.spnGroup)

        spnMember = v.findViewById(R.id.spnMember)

        var listGroup = ArrayList<String>()

        var listMember = ArrayList<User>()

        var listEmailMember = ArrayList<String>()

        groupArrayAdapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_dropdown_item, listGroup)

        spnGroup.adapter = groupArrayAdapter

        var memberAdapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_dropdown_item, listEmailMember)

        spnMember.adapter = memberAdapter

        var listKeyGroup = ArrayList<String>()

        queryGetIdUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var data: DataSnapshot? = null

                if (p0.children.iterator().hasNext()) {

                    data = p0.children.iterator().next()

                }

                currentHashId = data!!.child("hashId").value.toString()

                var currentEmail = data.child("email").value.toString()

                if (data == null) return

                var dataGroup = data!!.child("Group")

                for (tupleGroupOfUser in dataGroup.children) {

                    listKeyGroup.add(tupleGroupOfUser.key!!)

                }

                if(!dataGroup.exists()){

                    displayEventsWithouHavingGroup()

                }

                //Get all data in the group table data
                // to (filter get (all key groups) that user has registered)
                groupDatabase.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {

                        listCurrentUserGroup.clear()

                        var groupIterative = p0.children

                        for (dataGroup in groupIterative) {

                            //Get keys of group containing listkeyGroup
                            if (listKeyGroup.contains(dataGroup.key)) {

                                //get value of child having (name path)
                                listGroup.add(dataGroup.child("name").value.toString())

                                listCurrentUserGroup.add(dataGroup.getValue(Group::class.java)!!)

                            }

                        }

                        groupArrayAdapter.notifyDataSetChanged()

                        var listCorresPondingKeyUser = ArrayList<String>()

                        //When selecting your group you want to see members
                        //Updating member to spinner
                        spnGroup.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                                selectedGroup=listCurrentUserGroup.get(position)

                                listCorresPondingKeyUser.clear()

                                //Get (selected key group) from (spn)
                                var selectedKeyGroup = listKeyGroup.get(position)

                                groupDatabase.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(p0: DataSnapshot) {

                                        var dataTuples = p0.children

                                        var i=0

                                        for (tuple in dataTuples) {

                                            //It has only one case
                                            if (tuple.key.equals(selectedKeyGroup)) {

                                                for (user in tuple.child("User").children) {

                                                    //Getting (all key users) of (current group)
                                                    listCorresPondingKeyUser.add(user.key.toString())

                                                }

                                            }

                                        }

                                        databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot) {

                                                listMember.clear()

                                                listEmailMember.clear()

                                                var users = p0.children

                                                for (u in users) {

                                                    if (listCorresPondingKeyUser.contains(u.key)) {

                                                        var emailOfMember = u.child("email").value.toString()

                                                        var username=u.child("username").value.toString()

                                                        listMember.add(User(u.key.toString(), emailOfMember))

                                                        listEmailMember.add(username)

                                                    }

                                                }

                                                var currentU = User(currentHashId, currentEmail)

                                                var indexDelete=-1

                                                for(u in listMember.indices){

                                                    if(listMember.get(u).equals(currentHashId)) indexDelete=u

                                                }

                                                //Addinng position 0 (User)
                                                if(indexDelete!=-1){

                                                    listMember.removeAt(indexDelete)

                                                    listEmailMember.removeAt(indexDelete)

                                                    listMember.add(0, currentU)

                                                    listEmailMember.add(0,currentU.username)

                                                    memberAdapter.notifyDataSetChanged()

                                                }

                                                memberAdapter.notifyDataSetChanged()

                                                spnMember.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                                                    override fun onNothingSelected(parent: AdapterView<*>?) {

                                                    }

                                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                                                        //This field holds (hashId value) of (selected current member)
                                                        selectedCurrentMemberHashId = listMember.get(position).hashId

                                                        selectedIdUser=selectedCurrentMemberHashId

                                                        currentHashId = selectedCurrentMemberHashId

                                                        //In this case, we check fail although database hasn't tuple but even display as this
                                                        //Get all Event having (user) having (hashId) is similar to (selected user)
                                                        databaseEvents.ref.orderByChild("hashIdUser").equalTo(listMember.get(position).hashId)

                                                        databaseEvents.addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError) {

                                                            }

                                                            override fun onDataChange(p0: DataSnapshot) {

                                                                if (p0.hasChildren()) {

                                                                    //Thís attribute is used to check whether Realtime database having (Events)
                                                                    //Corresponding to (Selected user)
                                                                    //Event has (hashIdUser)
                                                                    var isExist = false

                                                                    var dataEvents = p0.children

                                                                    allItems.clear()

                                                                    for (e in dataEvents) {

                                                                        if (e.child("hashIdUser").value!!.equals(listMember.get(position).hashId)) {

                                                                            isExist = true

                                                                            var eventFb = e.getValue(EventFb::class.java)

                                                                            var event = Event(eventFb!!, eventFb!!.levelRecusion)

                                                                            allItems.add(event)

                                                                        }

                                                                    }

//                            adapter!!.notifyDataSetChanged()

                                                                    //If don't exist any item as this then (isExist=false)
                                                                    // --> adapter is empty
                                                                    //Because in this case, adapter is not allowed empty because multuply level
                                                                    //==> Try catch when occuring fail out
                                                                    if (isExist) fillAllItems(false, currentHashId, allItems)
                                                                    else {
                                                                        addOnlyCategoryToAllItems()
                                                                    }

                                                                } else {
                                                                    addOnlyCategoryToAllItems()
                                                                }

                                                            }

                                                        })

                                                    }

                                                })

                                            }

                                        })

                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                })

                            }

                        })

                    }

                    override fun onCancelled(p0: DatabaseError) {


                    }

                })

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })



        return v
    }

    fun displayEventsWithouHavingGroup(){
        var sharedPreferences=context!!.getSharedPreferences("CurrentUserInfo", Context.MODE_PRIVATE)

        var currentHashIdUser=sharedPreferences.getString("hashidUser","")

        selectedCurrentMemberHashId=currentHashId

        selectedIdUser=selectedCurrentMemberHashId

        //In this case, we check fail although database hasn't tuple but even display as this
        //Get all Event having (user) having (hashId) is similar to (selected user)
        databaseEvents.ref.orderByChild("hashIdUser").equalTo(currentHashIdUser)

        databaseEvents.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {

                    //Thís attribute is used to check whether Realtime database having (Events)
                    //Corresponding to (Selected user)
                    //Event has (hashIdUser)
                    var isExist = false

                    var dataEvents = p0.children

                    allItems.clear()

                    for (e in dataEvents) {

                        if (e.child("hashIdUser").value!!.equals(currentHashIdUser)) {

                            isExist = true

                            var eventFb = e.getValue(EventFb::class.java)

                            var event = Event(eventFb!!, eventFb!!.levelRecusion)

                            allItems.add(event)

                        }

                    }

//                            adapter!!.notifyDataSetChanged()

                    //If don't exist any item as this then (isExist=false)
                    // --> adapter is empty
                    //Because in this case, adapter is not allowed empty because multuply level
                    //==> Try catch when occuring fail out
                    if (isExist) fillAllItems(false, currentHashId, allItems)
                    else {
                        addOnlyCategoryToAllItems()
                    }

                } else {
                    addOnlyCategoryToAllItems()
                }

            }

        })
    }

    fun addOnlyCategoryToAllItems() {
        try {

            categoryDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    categories = ArrayList()

                    if (p0.hasChildren()) {

                        for (c in p0.children) {

                            var category = c.getValue(Category::class.java)

                            categories!!.add(category!!)

                        }

                        //Filter elements having
                        categories = categories!!.filter { it.hashIdUser.equals(currentHashId) } as ArrayList<Category>

                        //This code execute operation (add childs to allitems list)
                        //Looping for (All categories)
                        for (i in categories!!.indices) {

                            val category = categories!![i]

                            //Build a (CategoryItem) base (Category) (at index)
                            //Note that: This item has (CATEGORY_TYPE)
                            val categoryItem = Event(0, category.title, Category.CATEGORY_TYPE, -2, category.hasIdCategory, false, category.color)

                            //Getting id user to click add event into this category
                            categoryItem.hashIdUser=currentHashId

                            allItems.add(categoryItem)

                        }

                        try {

                            adapter?.notifyDataSetChanged()

                            adapter = EventsAdapter(this@CategoryFragment!!.context!!, allItems, this@CategoryFragment, recyclerView)

                            recyclerView!!.layoutManager = LinearLayoutManager(activity)

                            recyclerView!!.adapter = adapter

                            recyclerView.setToggleItemOnClick(false)

                            recyclerView.setAccordion(false)

                            recyclerView.openTill(0, 1, 2, 3)
                        } catch (ex: Exception) {
                            Toast.makeText(activity!!.applicationContext, "Danh sách trống", Toast.LENGTH_SHORT).show()
                        }

                    }

                }

            })

        } catch (e: Exception) {

            Toast.makeText(activity!!.applicationContext, "Danh sách trống", Toast.LENGTH_SHORT).show()

        }
    }

    fun assignAdapterWhenAllItemsIsEmpty() {

//        adapter = EventsAdapter(this!!.context!!, allItems, this,recyclerView)
//
//        recyclerView!!.layoutManager = LinearLayoutManager(activity)
//
//        recyclerView!!.adapter = adapter
//
//        recyclerView.setToggleItemOnClick(false)
//
//        recyclerView.setAccordion(false)
//
//        recyclerView.openTill(0,1,2,3)

    }

    //If the number of items for all category =0
    //Then show message adding...
    fun showCreateEventMessage() {
        if (allItems.size == 0) {

            tv_show_message!!.visibility = View.VISIBLE

        } else {

            tv_show_message!!.visibility = View.GONE

        }
    }

    //Check whether this application exists database???
    fun checkDatabase(): Boolean {

        val path = "/data/data/com.e15.alarmnats/databases/"

        val filename = "Remind"

        //Check (the file database)
        val file = File(path + filename)

        Log.d("Database", "File exists -> " + file.exists())

        return file.exists()

    }

    //Copy database to asset folder to watch data
    fun copyDatabase() {
        val path = "/data/data/com.e15.alarmnats/databases/Remind"
        val dbHandler = ReminderDatabase(this!!.context!!)
        dbHandler.getWritableDatabase()
        val fin: InputStream
        val fout: OutputStream
        val bytes = ByteArray(1024)
        try {
            fin = activity!!.assets.open("Remind")
            fout = FileOutputStream(path)
            var length = 0
//            while ((length = fin.read(bytes)) > 0) {
//                fout.write(bytes, 0, length)
//            }

            do {
                length = fin.read(bytes);

                if (length > 0) {
                    fout.write(bytes, 0, length);
                }

            } while (length > 0)

            fout.flush()
            fout.close()
            fin.close()
            Log.d("Database", "successfully copied database")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.d("Database", "-Error" + e.message)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("Database", "-Error" + e.message)
        }

    }


    //After starting we should (hide Keyboard)
    override fun onResume() {
        super.onResume()
        Utils.hideKeyboard(this!!.context!!)
    }

    //After item has added to database
    //We want to update (current) (RecycleView)
    //and move to the (precious position)
    fun refresh(item: Event) {

        var flag = false

        if (allItems.size == 0) flag = true

        categories!!.clear()
        allItems.clear()
        getAllEventFromDatabase(false)
//        allItems.addAll(items)

        if (flag) assignAdapterWhenAllItemsIsEmpty()

        //We put ? this here to avoid occur exception --> cash when allitems is empty
//        adapter?.notifyDataSetChanged()

//        recyclerView!!.scrollToPosition(searchPosition(item))

        showCreateEventMessage()

    }

    //Different from the (refresh method) in that
    // there is (no move) to the (previous position)
    fun refreshAfterRemovedCategory() {
        categories!!.clear()
        allItems.clear()
        getAllEventFromDatabase(false)
        showCreateEventMessage()
    }

    //Refresh edit
    fun editCategory(isEdit: Boolean) {

        var flag = false

        if (allItems.size == 0) flag = true

        categories!!.clear()
        //this here we don't clear allitems
        //We just clear it when in the end
        allItems.clear()
        getAllEventFromDatabase(isEdit)
//        val items = fillAllItems(isEdit)
//        allItems.addAll(items)

        if (flag) assignAdapterWhenAllItemsIsEmpty()

        //attribute adapter is init before
//        adapter!!.notifyDataSetChanged()
        showCreateEventMessage()
    }

    //Return (position) of (the item) (having key search)
    fun searchPosition(item: Event): Int {
        var pos = 0
        for (i in allItems.indices) {
            if (item.type === allItems[i].type && item.title.equals(allItems[i].title)) {
                pos = i
            }
        }
        return pos
    }

    fun getAllEventFromDatabase(isEdit: Boolean) {
        //In this case, we check fail although database hasn't tuple but even display as this
        //Get all Event having (user) having (hashId) is similar to (selected user)
        databaseEvents.ref.orderByChild("hashIdUser").equalTo(selectedCurrentMemberHashId)

        //(AddValueEventListener) because when data is changed then we (must update it)
        databaseEvents.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {

                    //Thís attribute is used to check whether Realtime database having (Events)
                    //Corresponding to (Selected user)
                    //Event has (hashIdUser)
                    var isExist = false

                    var dataEvents = p0.children

                    var tempAllItems = ArrayList(allItems)

                    for (e in dataEvents) {

                        //Data about current User is exists
                        if (e.child("hashIdUser").value!!.equals(selectedCurrentMemberHashId)) {

                            isExist = true

                            var eventFb = e.getValue(EventFb::class.java)

                            var event = Event(eventFb!!, eventFb!!.levelRecusion)

                            tempAllItems.add(event)

                        }

                    }

//                            adapter!!.notifyDataSetChanged()

                    //If don't exist any item as this then (isExist=false)
                    // --> adapter is empty
                    //Because in this case, adapter is not allowed empty because multuply level
                    //==> Try catch when occuring fail out
                    if (isExist) fillAllItems(isEdit, currentHashId, tempAllItems)
                    else {
                        try {

                            //Because before we have assigned adapter for RecycleView
                            adapter!!.notifyDataSetChanged()

                            adapter = EventsAdapter(this@CategoryFragment!!.context!!, allItems, this@CategoryFragment, recyclerView)

                            recyclerView!!.layoutManager = LinearLayoutManager(activity)

                            recyclerView!!.adapter = adapter

                            recyclerView.setToggleItemOnClick(false)

                            recyclerView.setAccordion(false)

                            recyclerView.openTill(0, 1, 2, 3)

                        } catch (e: Exception) {

                            Toast.makeText(activity!!.applicationContext, "Danh sách trống", Toast.LENGTH_SHORT).show()

                        }
                    }

                }else{
                    addOnlyCategoryToAllItems()
                }

            }

        })
    }

}