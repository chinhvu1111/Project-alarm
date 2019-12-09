package com.e15.alarmnats.adapter

import android.content.AsyncQueryHandler
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.Category
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.TaskManagementActivity
import com.e15.alarmnats.fragment.CategoryFragment
import com.e15.alarmnats.utils.AlarmReceiver
import com.e15.alarmnats.utils.Utils
import com.e15.alarmnats.view.ColorCircle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.multilevelview.MultiLevelAdapter
import com.multilevelview.MultiLevelRecyclerView
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.ArrayList


class EventsAdapter(private val context: Context, allItems: ArrayList<Event>,
                    private val categoryFragment: CategoryFragment,
                    mMultiLevelRecyclerView:MultiLevelRecyclerView) : MultiLevelAdapter(allItems) {

    //NOTE:
    //(All items) exists that contain both (Category) and (event)
    //(All of them) are displayed on the RecycleView
    var allItems = ArrayList<Event>()

    var hideOrShowListener: HideOrShowListener

    var lastPosition: Int = 0

    var flag: Boolean = false

    var dbHandler: ReminderDatabase

    var mMultiLevelRecyclerView:MultiLevelRecyclerView

    var firebaseDatabase:FirebaseDatabase

    lateinit var eventDatabase:DatabaseReference

    lateinit var categoryDatabase:DatabaseReference

    init {

        //Outer class of (this class) must implement (HideOrShowListener interface)
        hideOrShowListener = context as HideOrShowListener

        this.allItems = allItems

        dbHandler = ReminderDatabase(context)

        this.mMultiLevelRecyclerView=mMultiLevelRecyclerView

        firebaseDatabase= FirebaseDatabase.getInstance()

        eventDatabase=firebaseDatabase.getReference("Events")

        categoryDatabase=firebaseDatabase.getReference("Category")

    }

    //Using (CustomViewHolder)
    //This here we use bind on ViewHolder directly
    //This here we use (viewType) to distinguish between (two views)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View

        when (viewType) {

            CATEGORY_TYPE -> {

                //inflate (category item) contain title of category and button image
                view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false)

                return CategoryViewHolder(view)
            }

            EVENT_TYPE -> {

                //Item include startTime, endTime, title of this item
                view = LayoutInflater.from(context).inflate(R.layout.events_item, parent, false)

                return EventViewHolder(view)
            }
        }
        //In some cases, this line can be ignore because or (not)
        return CategoryViewHolder(parent)
    }

    fun setExpandButton(expandButton:ImageView,isExpanded:Boolean){

        if(isExpanded){

            expandButton.setImageResource(R.drawable.ic_arrow_down_white_24dp)

        }
        else{

            expandButton.setImageResource(R.drawable.ic_arrow_up_white_24dp)

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //This code meaning each time we click expansion button then (allitems will add child items to allitems)
        //get item at position in the list of items
        val item = allItems!!.get(position)

        when (item.type) {

            CATEGORY_TYPE -> {

                (holder as CategoryViewHolder).tvCategoryTitle.setText(item.title)
                holder.categoryColorIcon?.setColor(Color.parseColor(item.color))

                //When clicking (edit button)
                if (item.isEdit) {
                    holder.imgAddEvent.visibility = View.GONE
                    holder.btnDeleteCat.visibility = View.VISIBLE
                    holder.btnEditCat.visibility = View.VISIBLE


                    //When clicking editImage or EditButton Category
                    holder.btnEditCat.setOnClickListener {

                        //Category class include (id, title, color, type)
                        val category = Category(item.hashIdCategory, item.title!!, item.color!!,0)

                        //Show dialog to (update category) same as (adding category)
                        //but diference from this, the text is (filled automatically)
                        hideOrShowListener.showEditCategoryDialog(category)

                    }

                    holder.btnDeleteCat.setOnClickListener {

                        //show confirm dialog to be sure want to delete this category
                        showConfirmDialog(item)

                    }

                } else {

                    //Display normally
                    holder.imgAddEvent.visibility = View.VISIBLE
                    holder.btnDeleteCat.visibility = View.GONE
                    holder.btnEditCat.visibility = View.GONE

                    holder.imgAddEvent.setOnClickListener { hideOrShowListener.setHideOrShow(item, false) }
                }
            }


            EVENT_TYPE -> {


                val startTime = item.startTime!!.trim().split(":")
                val endTime = item.endTime!!.trim().split(":")
                val date = item.date!!.trim().split("/")
                val startHour = Integer.parseInt(startTime[0])
                val endHour = Integer.parseInt(endTime[0]);
                val endMinute = Integer.parseInt(endTime[1])

                var evenHolder = holder as EventViewHolder

                //When clicking (expands items)
                if(item.hasChildren()&&item.children.size>0){

                    setExpandButton(evenHolder.mExpandIcon, item.isExpanded)

                    evenHolder.mExpandButton.isVisible=true

                }else{
                    evenHolder.mExpandButton.isVisible=false
                }

                //Indent child items
                //DisplayMetrics
                //Return the current display metrics that are in effect for this resource object.
                // The returned object should be treated as read-only.
                //density
                //The logical density of the display. This is a scaling factor for the
                // Density Independent Pixel unit, where one DIP is one pixel on an
                // approximately 160 dpi screen (for example a 240x320, 1.5"x2" screen),
                // providing the baseline of the system's display. Thus on a 160dpi screen
                // this density value will be 1; on a 120 dpi screen it would be .75; etc.
                var density=context.resources.displayMetrics.density

                ((evenHolder.containerLayout.layoutParams)as ViewGroup.MarginLayoutParams).leftMargin= (allItems.get(position).levelRecusion * 20 * density + 0.5f).toInt()+30;

                //------------------------------

                evenHolder.tvEventName.setText(item.title)

                if (item.isDone == -1) {

                    evenHolder.imgDone.isVisible = false
                    evenHolder.imgDone.isClickable = false

                    evenHolder.imgPlay.isVisible = true
                    evenHolder.imgPlay.isClickable = true

                } else {

                    evenHolder.imgDone.isVisible = true
                    evenHolder.imgDone.isClickable = true

                    evenHolder.imgPlay.isVisible = false
                    evenHolder.imgPlay.isClickable = false

                }

                var textStart: String = ""

                if (startHour < 12) {
                    textStart = startHour.toString() + " : " + startTime[1] + " am "
                } else {
                    textStart = (startHour - 12).toString() + " : " + startTime[1] + " pm "
                }

                var textEnd: String = ""

                if (endHour < 12) {

                    textEnd = endHour.toString() + " : " + endMinute + " am"

                } else {

                    textEnd = (endHour - 12).toString() + " : " + endMinute + " pm"

                }

                holder.tvTime.text = textStart + "\n" + textEnd

                Log.e("---------Time viewer", endHour.toString() + " " + endMinute)

                holder.tvDate.setText(Utils.months[Integer.parseInt(date[0]) - 1] + " " + date[1])

                holder.tvPlace.setText(item.place)

                if (item.description!!.length == 0) {

                    item.description = "Mô tả chi tiết trống"

                }
                holder.tvDescription.setText(item.description)

                holder.colorCircle!!.setColor(Color.parseColor(item.color))

                //Related Progressbar

                var difference:Double=0.toDouble()

                //RemainingTime==-1 or (not) it is not important
                //Total of timer always is endTime - startTime --> Because when initing new task
                //endTime = startTime + (intervalTime)
                var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm");

                var startTimeProgressBar = simpleDateFormat.parse(item.startTime);

                var endTimeProgressBar = simpleDateFormat.parse(item.endTime);

                difference = (endTimeProgressBar.time - startTimeProgressBar.time).toDouble()

                holder.progressBarTask.max=100

                var itemRemainngTimerIncludeSubItem:Long=0

                //If current item has childs then we caculate (all childs remaining Timers)
//                itemRemainngTimerIncludeSubItem=caculateAllRemainingTimeOfChilds(item,allItems)

                var percentTimer:Long=0

                //If parent task hasn't run
                //We must caculate all (working timer) of (all childs of its)
                if(!item.hasChildren()){
                    if(item.remainingTime==-1.toLong()){

                        percentTimer=0

                    }else{

                        percentTimer=Math.round((difference-item.remainingTime)/difference*100)

                    }
                }
                else{

                    percentTimer=Math.round((caculateAllRemainingTimeOfChilds(item,allItems)/difference*100))

                }

                holder.progressBarTask.progress=percentTimer.toInt()

                holder.tvPercentTask.setText(percentTimer.toString()+"%")

                //Each time we tap on the RecycleView
                // onTouchListener() will update adapter of RecycleView
                //onDataSetChanged() then call this condition to (check)
                if (item.isShow) {

                    holder.tvDescription.visibility = View.VISIBLE

                    //TextView.animate()
                    //This method returns a (ViewPropertyAnimator) object, which can be used to animate (specific properties) on this View.
                    holder.tvDescription.animate().alpha(1f)
                            .setDuration(200).setInterpolator(AccelerateInterpolator()).start()

                    holder.containerLayout.isSelected = true

                    holder.tvEdit.visibility = View.VISIBLE

                } else {

                    //Hide the content of (TextView description)

                    holder.tvDescription.visibility = View.GONE

                    holder.tvDescription.animate().alpha(0f).setDuration(500).start()

                    //Changes the selection state of this view.
                    // A view can be selected or not.
                    // Note that selection is (not the same) as focus.
                    // Views are typically selected in the context of an AdapterView like ListView or GridView;
                    // the selected view is the view that is (highlighted).
                    holder.containerLayout.isSelected = false

                    //Display (edit button)
                    holder.tvEdit.visibility = View.INVISIBLE
                }

                holder.tvEdit.setOnClickListener { hideOrShowListener.setHideOrShow(item, true) }

            }
        }
    }

    //This function is used to caculate all remaining time childs
    fun caculateAllRemainingTimeOfChilds(currentTime:Event, allItems: ArrayList<Event>):Long{

        var finalTotal:Long=0

        for(i in allItems){

            //Getting (current item) from (allitems)
            if(i.hashId.equals(currentTime.hashId)){

                var totalchilds=0.toLong()

                var workingTimeOfCurrenTask:Long=0

                if(i.hasChildren()){

                    for (child in i.children){

                        totalchilds+=caculateAllRemainingTimeOfChilds(child as Event,i.children as ArrayList<Event>)

                        child as Event

                        //It is used to hold (all interval time of childs)
                        var totalIntervalStartEndTime=0.toLong()

                        //This is used to updating workingTime of (a current task) each times
                        for(subOfSubItem in i.children){

                            subOfSubItem as Event

                            var formatter=SimpleDateFormat("HH:mm")

                            var startTime=formatter.parse(subOfSubItem.startTime)

                            var endTime=formatter.parse(subOfSubItem.endTime)

                            totalIntervalStartEndTime+=endTime.time-startTime.time

                        }

                        //Hold all (inteval time) of (the current child task)
                        var formatter=SimpleDateFormat("HH:mm")

                        var startTime=formatter.parse(i.startTime)

                        var endTime=formatter.parse(i.endTime)

                        var difference=endTime.time-startTime.time

                        //this parameter is used to save (total of all childs interval time)
//                        totalIntervalTimeOfChildTasks+=difference

                        //If (remainintTime ==-1) it means that this (current child) task (has run)
                        if(i.remainingTime!=-1.toLong()&&i.remainingTime!=difference-totalIntervalStartEndTime){

                            //We holds (all time) of (current task)
                            // - (total (all times) of child tasks)
                            // - (remainingTime)
                            totalchilds+=difference-totalIntervalStartEndTime-i.remainingTime

                        }

                    }

                }

                //Updating ignores Category
                //For child task hasn't childs
                // (child has run but not having subchild task)
                if(!i.hasChildren()&&i.type!=0){

                    //These lines are used to caculate working time of current task
                    var formatter=SimpleDateFormat("HH:mm")

                    var startTime=formatter.parse(i.startTime)

                    var endTime=formatter.parse(i.endTime)

                    var difference=endTime.time-startTime.time

                    if(i.remainingTime!=-1.toLong()&&i.remainingTime!=difference){

                        workingTimeOfCurrenTask=difference-i.remainingTime

                    }

                }

                finalTotal+=totalchilds+workingTimeOfCurrenTask

            }
        }

        return finalTotal

    }

    //Show confirm with (item) as parameter in the (showConfirmDialog function)
    fun showConfirmDialog(item: Event) {

        //A subclass of Dialog that can display one, two or three buttons.
        // If you only want to display a String in this dialog box, use the setMessage() method.
        // If you want to display a more complex view,
        // look up the FrameLayout called "custom" and add your view to it:
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle(R.string.confirm_sure)

        alertDialogBuilder.setMessage(R.string.delete_cofirm_message)

        //Set a listener to be invoked when (the positive button) of the (dialog) is pressed
        alertDialogBuilder.setPositiveButton(R.string.yes,

                DialogInterface.OnClickListener { arg0, arg1 ->
                    //find all events in deleted category and cancel all alarm
                    val receiver = AlarmReceiver()

                    val category = item.category

                    for (i in allItems!!.indices) {

                        val item = allItems!!.get(i)

                        //Hold all items in the list of (items events/tasks) orther than (Category items)
                        if (item.type === EVENT_TYPE) {

                            if (item.category.equals(category)) {

                                receiver.cancelAlarm(context, item.requestCode)

                            }
                        }
                    }

                    dbHandler.removeCategory(item.hashIdCategory, item.title!!)

                    //Removing category from Realtime database
                    categoryDatabase.child("${item.hashIdCategory}").removeValue()

                    //Updating here to refresh all RecycleView
                    categoryFragment.refreshAfterRemovedCategory()

                    //This code maybe occur fail
                    (context as TaskManagementActivity).isEditMode = (false)

                    Utils.showToastMessage("Đã xóa!", context)
                })
        alertDialogBuilder.setNegativeButton(R.string.no,
                DialogInterface.OnClickListener { dialog, which -> })

        //Creates an (AlertDialog) with the (arguments) supplied to this builder.
        //Calling this method does (not) display the dialog.
        // If no additional processing is needed,
        // show() may be called instead to both create and display the dialog.
        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
    }

    override fun getItemCount(): Int {

        Log.d("c", allItems!!.size.toString() + "")

        return allItems!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (allItems != null) {

            allItems!!.get(position).type

        } else 0
    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryTitle: TextView
        val categoryColorIcon: ColorCircle?
        val imgAddEvent: ImageView
        val btnDeleteCat: ImageView
        val btnEditCat: ImageView

        init {

            tvCategoryTitle = itemView.findViewById<View>(R.id.tvCategoryTitle) as TextView

            categoryColorIcon = itemView.findViewById<View>(R.id.categoryColorIcon) as ColorCircle?

            imgAddEvent = itemView.findViewById<View>(R.id.addEvent) as ImageView

            btnDeleteCat = itemView.findViewById<View>(R.id.imgDeleteCat) as ImageView

            btnEditCat = itemView.findViewById<View>(R.id.imgEditCat) as ImageView

        }
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnCreateContextMenuListener {
        val descriptionLayout: LinearLayout
        val containerLayout: LinearLayout
        val eventContainer: RelativeLayout
        val tvEventName: TextView
        val tvTime: TextView
        val tvDate: TextView
        val tvPlace: TextView
        val tvDescription: TextView
        val tvEdit: TextView
        var colorCircle: ColorCircle?
        lateinit var imgDone: ImageView
        lateinit var imgPlay: ImageView
        lateinit var mExpandIcon:ImageView
        lateinit var mExpandButton:LinearLayout

        lateinit var tvPercentTask:TextView
        lateinit var progressBarTask:ProgressBar

        private val onEditMenu = MenuItem.OnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> hideOrShowListener.setHideOrShow(allItems!!.get(adapterPosition), true)
                1 -> {

                    //Delete task from (this current position)
                    //Delete this task is refer to (delete) it's (all childs)

                    var currentDeletingItem=allItems.get(adapterPosition)

                    //If current task has (a parent task)
                    if(!currentDeletingItem.parentId.equals("")){

                        var parentId=allItems.get(adapterPosition).parentId

                        var parentEvent=dbHandler.getEventId(parentId)

                        //In order to check whether any task has run
                        //(RemainingTime ==-1) and (difference - total of interval time child != remainingTime)
                        parentEvent!!.remainingTime+=updatingAllChildsWhenDeleting(currentDeletingItem)

                        //Updating (the parent) of (a current task)
                        dbHandler.updateEvent(parentEvent!!)

                        //Updating (the parent) of (a current task) from Realtime database
                        eventDatabase.ref.child("${parentEvent.hashId}/remainingTime").setValue(parentEvent.remainingTime)

                        //Updating (remainingTime) of (the parent) of (the current item)
                        allItems.forEach { item: Event ->
                            if(item.hashId.equals(currentDeletingItem.parentId)){

                                item.remainingTime=parentEvent!!.remainingTime

                            }
                        }

                        //Delete all childs corresponding to (all items are changed by the library)
                        deleteNextAllChildTask(dbHandler,allItems,adapterPosition,0)

                        //If we put this line here --> it causes errors
                        //delete all childs of current item in the list
                        findIdOfTask(allItems,currentDeletingItem.parentId,currentDeletingItem.levelRecusion,currentDeletingItem)

                        //If currentDeleltingItem via two funtions that hasn't been deleted --> Delete
                        allItems.remove(currentDeletingItem)

                    }

//                    //Delete (all childs) of (current item)
//                    deleteAllChilds(dbHandler,allItems.get(adapterPosition))
//
                    //In case, (levelRecusion=0) (the most huge parent)
                    if(currentDeletingItem.parentId.equals("")) {


                        //This code must put before (deleteAllchildsOfAnyMostParent)
                        //If we want to delete child then we must have child --> (deleteAllchildsOfAnyMostParent --> Clear child) --> Don't delete (from database)

//                        //If we put this line here --> it causes errors
//                        //delete all childs of current item in the list
                        deleteAllchildsOfAnyMostParentRecusive(dbHandler,allItems,allItems!!.get(adapterPosition))

                        //This line to (remove child items) (when expanding)
                        deleteAlllChildsIfExpansion(allItems,allItems.get(adapterPosition),adapterPosition)

                        //Remove (current item)
                        dbHandler.removeEvent(allItems!!.get(adapterPosition).hashId)

                        //Remove (current item) from Realtime database
                        eventDatabase.child(allItems!!.get(adapterPosition).hashId).removeValue()

                        AlarmReceiver().cancelAlarm(context,allItems.get(adapterPosition).requestCode)

                        allItems.removeAt(adapterPosition)

                    }
                    Utils.showToastMessage("Deleted", context)

                    //If task has (level==0) then we delete (immediately)
//                    allItems!!.removeAt(adapterPosition)

                    notifyDataSetChanged()

                    hideOrShowListener.refreshAllEventFragment()

                }
                2->{

                    var currentItem=allItems.get(adapterPosition)

                    if(currentItem.isDone==-1&&currentItem.remainingTime==-1.toLong()){

                        var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm");

                        var startTime = simpleDateFormat.parse(currentItem.startTime);
                        var endTime= simpleDateFormat.parse(currentItem.endTime);

                        var difference = (endTime.time - startTime.time).toDouble()

                        currentItem.remainingTime=difference.toLong()

                        dbHandler.updateEvent(currentItem)

                        //Updating remainingTime of event when (updating)
                        //This causes (percent is negative)
                        eventDatabase.child("${currentItem.hashId}/remainingTime").setValue(currentItem.remainingTime)

                        hideOrShowListener.createNewSubTask(currentItem,currentItem.levelRecusion+1)

                    }else if(currentItem.remainingTime<1000*60){

                        Toast.makeText(context,"Tác vụ còn lại thời gian dưới 1 phút không thể tạo tác vụ con với thời gian nhỏ hơn", Toast.LENGTH_SHORT).show()

                    }
                    else{

                        hideOrShowListener.createNewSubTask(currentItem,currentItem.levelRecusion+1)

                        hideOrShowListener.refreshAllEventFragment()

                    }

                }
                3 -> {


                    if (allItems.get(adapterPosition).remainingTime == 0.toLong()) {

                        Toast.makeText(context, "Tác vụ đã hoàn thành!", Toast.LENGTH_SHORT).show()

                    } else if (allItems.get(adapterPosition).remainingTime == -1.toLong()) {

                        AlertDialog.Builder(context, R.style.MyDialogTheme).setMessage("Tác vụ vị trí $adapterPosition là tác vụ mới, bạn có muốn bắt đầu?"
                        ).setPositiveButton("Có", DialogInterface.OnClickListener { dialog, which ->
                            hideOrShowListener.continueTask(allItems.get(adapterPosition))
                        }).setNegativeButton("Không", null).show()

                    } else {

                        //adapterPosition
                        //Returns the (Adapter position) of the item represented by this ViewHolder.
                        //Note that this might be (different than) the <getLayoutPosition>()
                        // if there are (pending adapter updates) but a (new layout pass) has not happened yet.
                        AlertDialog.Builder(context, R.style.MyDialogTheme).setMessage("Tác vụ vị trí $adapterPosition còn ${splitToComponentTimes(allItems.get(adapterPosition).remainingTime/1000)} " +
                                "bạn có muốn tiếp tục?").setPositiveButton("Có", DialogInterface.OnClickListener { dialog, which ->
                            hideOrShowListener.continueTask(allItems.get(adapterPosition))
                        }).setNegativeButton("Không", null).show()

                    }

                }
            }
            true
        }

        //new conclusion: object always refer to same (memory cell)
        //Updating base on (remaining Time) and (don't care) about (working time)
        fun updatingAllChildsWhenDeleting(currentDeletingItem: Event):Long{

            var totalUpdating:Long=0

            //If we recusive to (a final recursion)
            if(!currentDeletingItem.hasChildren()){

                if(currentDeletingItem.remainingTime!=-1.toLong()){

                    return currentDeletingItem.remainingTime

                }else{
                    var formatter=SimpleDateFormat("HH:mm")

                    var startTime=formatter.parse(currentDeletingItem.startTime)

                    var endTime=formatter.parse(currentDeletingItem.endTime)

                    return endTime.time-startTime.time
                }

            }

            for(child in currentDeletingItem.children){

                var remainingTimeChild=updatingAllChildsWhenDeleting(child as Event)

                if(child.remainingTime!=-1.toLong()){

                    totalUpdating+=remainingTimeChild

                }

            }

            currentDeletingItem.remainingTime+=totalUpdating

            return currentDeletingItem.remainingTime

        }

        init {
            descriptionLayout = itemView.findViewById<View>(R.id.descriptionLayout) as LinearLayout
            containerLayout = itemView.findViewById<View>(R.id.container) as LinearLayout
            eventContainer = itemView.findViewById<View>(R.id.eventContainer) as RelativeLayout
            tvEventName = itemView.findViewById<View>(R.id.tvEventName) as TextView
            tvTime = itemView.findViewById<View>(R.id.tvTime) as TextView
            tvDate = itemView.findViewById<View>(R.id.tvDate) as TextView
            tvPlace = itemView.findViewById<View>(R.id.tvPlace) as TextView
            tvDescription = itemView.findViewById<View>(R.id.tvDescription) as TextView
            tvEdit = itemView.findViewById<View>(R.id.tvEdit) as TextView
            colorCircle = itemView.findViewById<View>(R.id.color_circle) as ColorCircle?

            imgDone = itemView.findViewById(R.id.imgDone)
            imgPlay = itemView.findViewById(R.id.imgPlay)

            imgPlay.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    //If remainingTime==0 --> done
                    //If remainingTime==-1 --> is not run
                    //If remainingTim>0 --> have run --> using RemainingTime to run
                    if (allItems.get(adapterPosition).remainingTime == 0.toLong()) {

                        Toast.makeText(context, "Tác vụ đã hoàn thành!", Toast.LENGTH_SHORT).show()

                    } else if (allItems.get(adapterPosition).remainingTime == -1.toLong()) {

                        AlertDialog.Builder(context, R.style.MyDialogTheme).setMessage("Tác vụ vị trí $adapterPosition là tác vụ mới, bạn có muốn bắt đầu?"
                        ).setPositiveButton("Có", DialogInterface.OnClickListener { dialog, which ->
                            hideOrShowListener.continueTask(allItems.get(adapterPosition))
                        }).setNegativeButton("Không", null).show()

                    } else {

                        //adapterPosition
                        //Returns the (Adapter position) of the item represented by this ViewHolder.
                        //Note that this might be (different than) the <getLayoutPosition>()
                        // if there are (pending adapter updates) but a (new layout pass) has not happened yet.
                        AlertDialog.Builder(context, R.style.MyDialogTheme).setMessage("Tác vụ vị trí $adapterPosition còn ${splitToComponentTimes(allItems.get(adapterPosition).remainingTime/1000)} " +
                                "bạn có muốn tiếp tục?").setPositiveButton("Có", DialogInterface.OnClickListener { dialog, which ->
                            hideOrShowListener.continueTask(allItems.get(adapterPosition))
                        }).setNegativeButton("Không", null).show()

                    }
                }

            })

            mExpandIcon=itemView.findViewById(R.id.mExpandIcon)

            mExpandButton=itemView.findViewById(R.id.expand_field)

            tvPercentTask=itemView.findViewById(R.id.tvPercentTask)
            progressBarTask=itemView.findViewById(R.id.progressBarTask)

            mExpandButton.setOnClickListener(object:View.OnClickListener{
                override fun onClick(v: View?) {

                    mMultiLevelRecyclerView.toggleItemsGroup(adapterPosition)

                    if(allItems.get(adapterPosition).isExpanded){

                        mExpandIcon.animate().rotation(-180.toFloat()).start()

                    }else{

                        mExpandIcon.animate().rotation(0.toFloat()).start()

                    }


                }
            })

            eventContainer.setOnClickListener(this)
            eventContainer.setOnCreateContextMenuListener(this)
            descriptionLayout.setOnCreateContextMenuListener(this)
        }

        override fun onClick(v: View) {
            if (flag) {
                allItems!!.get(lastPosition).isShow = false
            }
            allItems!!.get(adapterPosition).isShow = true
            flag = true
            lastPosition = adapterPosition
            notifyDataSetChanged()
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu.setHeaderTitle("Chọn tác vụ")
            val Edit = menu.add(Menu.NONE, 0, 0, context.resources.getString(R.string.menu_edit))
            val Delete = menu.add(Menu.NONE, 1, 0, context.resources.getString(R.string.menu_delete))
            var addNewSubEvent= menu.add(Menu.NONE,2,0,context.resources.getString(R.string.add_sub_task))
            val continueTask = menu.add(Menu.NONE, 3, 0, context.resources.getString(R.string.menu_continue))
            Edit.setOnMenuItemClickListener(onEditMenu)
            Delete.setOnMenuItemClickListener(onEditMenu)
            addNewSubEvent.setOnMenuItemClickListener(onEditMenu)
            continueTask.setOnMenuItemClickListener(onEditMenu)
        }
    }

    //Function of this interface is used to hide/show caculate icon
    interface HideOrShowListener {
        fun setHideOrShow(item: Event, isEdit: Boolean)
        fun showEditCategoryDialog(category: Category)
        fun continueTask(item: Event)
        fun createNewSubTask(item: Event,level:Int)
        fun refreshAllEventFragment()
    }

    companion object {

        val EVENT_TYPE = 1

        val CATEGORY_TYPE = 0
    }

    fun splitToComponentTimes(biggy:Long):String{

        val longVal = biggy
        val hours = longVal / 3600
        var remainder = longVal - hours * 3600
        val mins = remainder / 60
        remainder = remainder - mins * 60
        val secs = remainder

        return "$hours giờ $mins phút và $secs giây"

    }

    fun findIdOfTask(listItem:ArrayList<Event>,parentId:String,levelRecursive:Int,currentDeletingItem:Event){

        for(item in listItem){

            if(item.levelRecusion!=levelRecursive-1){

                if(item.hasChildren()) {

                    findIdOfTask(item.children as ArrayList<Event>,parentId,levelRecursive,currentDeletingItem)

                }

            }else{
                if(item.hashId.equals(parentId)){

                    for(i in item.children){

                        if((i as Event).hashId.equals(currentDeletingItem.hashId)){

                            var indexOfParent=listItem.indexOf(item)

                            var subItem=i as Event

                            //Get (position) of list of child items in childs of (parent items)
                            var index=item.children.indexOf(subItem)

                            //TO delete all --> must use (t object)
                            //Delete all childs of (deleting current task)
                            if(listItem.get(indexOfParent).children.get(index).children!=null){

                                listItem.get(indexOfParent).children.get(index).children.clear()

                            }

                            listItem.get(indexOfParent).children.remove(i)

                            break;

                        }

                    }

                }
            }

        }

    }

    fun deleteAlllChildsIfExpansion(allItems: ArrayList<Event>, parentId: Event, position: Int){

        if(allItems.size>position+1&&allItems.get(position+1).parentId.equals(parentId.hashId)){

            if(allItems.size>position+2) deleteAlllChildsIfExpansion(allItems,allItems.get(position+1),position+1)

            //Cancel alarm base on id
            AlarmReceiver().cancelAlarm(context,allItems.get(position+1).requestCode)

            allItems.removeAt(position+1)

        }

    }

    //Delete (all childs<Recursion>) of (the most parent task) --> Not expand
    fun deleteAllchildsOfAnyMostParentRecusive(dbHandler: ReminderDatabase,allItems: ArrayList<Event>, parentId: Event){

        var postionParent=allItems.indexOf(parentId)

        //Parent hasn't any childs
        if(parentId.children!=null){
            //Delete all childs from database
            for(item in parentId.children){

                item as Event

                if(item.hasChildren()){

                    deleteAllchildsOfAnyMostParentRecusive(dbHandler, item.children as ArrayList<Event>, item)

                    item.children.clear()

                }

                dbHandler.removeEvent(item.hashId)

                //Removing event from Realtime database
                eventDatabase.child(item.hashId).removeValue()

            }
        }

        //This here can be caused error when deleting item not having any child but it is parent item having level=0
        //Delete all child from parent
        if(postionParent!=-1&&parentId.children!=null) allItems.get(postionParent).children.clear()

        //Deletign (parent event) from database
        dbHandler.removeEvent(parentId.hashId)

        //Removing (parent event) from Realtime database
        eventDatabase.child(parentId.hashId).removeValue()

    }

    fun deleteNextAllChildTask(dbHandler: ReminderDatabase, allItems: ArrayList<Event>, position: Int,count:Int){

        var i=allItems.get(position)

        //Cancel (all alarm childs)
        AlarmReceiver().cancelAlarm(context, i.requestCode)

        if(count!=0) {

            allItems.removeAt(position)

            notifyItemRemoved(position)

        }

        dbHandler.removeEvent(i.hashId)

        //Remove event from Realtime database
        eventDatabase.child(i.hashId).removeValue()

        //Checking whether current item has child
        //Then (deleting) (all childs) of it
        if(i.hasChildren()){
            deleteNextAllChildTask(dbHandler, allItems, position,count+1)
        }

    }

}
