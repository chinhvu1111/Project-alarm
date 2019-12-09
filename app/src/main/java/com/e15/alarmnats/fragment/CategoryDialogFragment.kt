package com.e15.alarmnats.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.fragment.app.DialogFragment;
import com.e15.alarmnats.Model.Category
import com.e15.alarmnats.Model.NotifyInterface
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.Model.EventFb
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.ColorsAdapter
import com.e15.alarmnats.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

//This class inflate (adding layout category)
class CategoryDialogFragment : DialogFragment() {

    private var category: Category? = null

    //(List of Categorys) contain (name, color) as colors
    //Through this attributes we will (update database)
    private val colors = ArrayList<Colors>()

    //layout of list of colors
    private var colorGrids: GridView? = null
    private var inputText: EditText? = null

    //Position what you want to add
    private var pos: Int = 0

    //adaper of (grid colors)
    private var adapter: ColorsAdapter? = null

    private var isError = true

    private var isEdit: Boolean = false

    private var notifyInterface: NotifyInterface? = null

    //Get (all colors) from (the Resource)
    private var color: Array<String>? = null

    lateinit var firebaseDatabase:FirebaseDatabase

    lateinit var categoryDatabase:DatabaseReference

    lateinit var auth:FirebaseAuth

    lateinit var databaseUser:DatabaseReference

    lateinit var currentUser:FirebaseUser

    lateinit var databaseEvent:DatabaseReference

    override fun onAttach(context: Context) {

        super.onAttach(context)

        notifyInterface = activity!! as NotifyInterface?

        //Array of (Int values) that correspond to the color
        color = context!!.resources.getStringArray(R.array.colors)

        firebaseDatabase= FirebaseDatabase.getInstance()

        auth=FirebaseAuth.getInstance()

        databaseUser=firebaseDatabase.getReference("User")

        databaseEvent=firebaseDatabase.getReference("Events")

        currentUser=auth.currentUser!!

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val bdn = arguments

        //Get from (bundle) of (this fragment)
        this.isEdit = bdn!!.getBoolean("ISEDIT")

        val hashIdCategory = bdn.getString("HASHID")

        val title = bdn.getString("TITLE")

        val color = bdn.getString("COLOR")

        this.category = Category(hashIdCategory!!, title, color,0)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.category_dialog, null)

        //Style for setStyle(int, int): don't include a title area.
        //dialog
        //Return the (Dialog) this fragment is currently (controlling).
        dialog!!.requestWindowFeature(DialogFragment.STYLE_NO_TITLE)

        //getWindow()
        //Retrieve the current Window for the activity.
        // This can be used to directly access parts of the Window API
        // that are not available through Activity/Screen.

        //setBackGroundDrawable
        //Change the background of this window to a (custom Drawable).
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        colorGrids = v.findViewById(R.id.colorGrid)

        inputText = v.findViewById(R.id.inputName)

        if (isEdit) {

            //if (isEdit==true)  color is hold base on data item in the database
            pos = findPosition(category!!.color)

        } else {

            //generating automatically
            pos = Random().nextInt(7)

        }
        addColors(pos)
        adapter = ColorsAdapter(activity!!, colors)
        colorGrids!!.adapter = adapter


        colorGrids!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            colors.clear()
            addColors(position)
            adapter!!.notifyDataSetChanged()
            pos = position
        }

        inputText!!.setText(category!!.title)

        inputText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            //If length of input text over 20 characters
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (inputText!!.text.toString().trim { it <= ' ' }.length > 20) {
                    inputText!!.error = "Tên quá dài"
                    isError = true
                }
                //If input is ""
                if (inputText!!.text.toString().trim { it <= ' ' }.length == 0) {
                    isError = true
                    inputText!!.error = "Tên không hợp lệ"
                } else {
                    isError = false
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        val btnSave = v.findViewById(R.id.btnSave) as Button

        //Saving category
        btnSave.setOnClickListener {

            val text = inputText!!.text.toString().trim { it <= ' ' }

            if (text.length == 0 && isError) {

                inputText!!.error = "Tên không hợp lệ"

                isError = true

                //insert category into database
            } else if (text.length != 0 && !isError) {

                val title = text.substring(0, 1).toUpperCase() + text.substring(1)

                val item = Event(0,title, colors[pos].name!!, Category.CATEGORY_TYPE, false,0)

                categoryDatabase=firebaseDatabase.getReference("Category")

                val dbHandler = ReminderDatabase(this!!.context!!)

                var res = false

                //If we are creating new category
                if (!isEdit) {

                    var queryIdUser=databaseUser.orderByChild("email").equalTo(currentUser.email)

                    //Creating (Category) base on (UserHashId)
                    queryIdUser.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            //data is used to get (the information) of (current user)
                            var data=p0.children.iterator().next()

                            var currentUser=data.child("hashId").value.toString()

                            //Creating new category from realtime database
                            var hasIdCategory=categoryDatabase.push().key

                            var category=Category(hasIdCategory!!,title,colors[pos].name!!,currentUser)

                            categoryDatabase.child(hasIdCategory!!).setValue(category)

                            res = dbHandler.createNewCategory(category)

                            Utils.showToastMessage("New Category Created", this@CategoryDialogFragment!!.context!!)

                            //We notices when creating new category
                            if (res) {
                                notifyInterface!!.onInserted(item)
                            } else {
                                notifyInterface!!.showExistingDialog(item.title!!, item.color!!)
                            }

                            //Dismiss this dialog, removing it from the screen.
                            // This method can be invoked safely from any thread.
                            dialog!!.dismiss()

                            //After adding we (hide keyboard)

                            Utils.hideKeyboard(this@CategoryDialogFragment!!.context!!, inputText!!)

                        }

                    })
                } else {

                    var queryIdUser=databaseUser.orderByChild("email").equalTo(currentUser.email)

                    queryIdUser.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            //In this case: title is (new title)
                            //category.title is the (old title)
                            res = dbHandler.updateCategory(category!!.hasIdCategory, title, colors[pos].name!!, category!!.title)

                            var data=p0.children.iterator().next()

                            var idCurrentUser=data.child("hashId").value.toString()

                            var newCategory=Category(category!!.hasIdCategory,title,colors[pos].name!!,idCurrentUser)

                            //Updating category by using object
                            categoryDatabase.ref.child(newCategory!!.hasIdCategory).setValue(newCategory)

                            Utils.showToastMessage("Category Edited", this@CategoryDialogFragment!!.context!!)

                            //This database need to add HashIdUser
                            //get all events having (old title)
                            val items = dbHandler.getItemByCategory(category!!.title)

                            for (event in items) {

                                event.category = title

                                dbHandler.updateItem(event)

                                //Converting
                                var eventFb=EventFb(event)

                                //Udating events from realtime database
                                databaseEvent.ref.child(event.hashId).setValue(eventFb)

                            }

                            //We notices when creating new category
                            if (res) {
                                notifyInterface!!.onInserted(item)
                            } else {
                                notifyInterface!!.showExistingDialog(item.title!!, item.color!!)
                            }

                            //Dismiss this dialog, removing it from the screen.
                            // This method can be invoked safely from any thread.
                            dialog!!.dismiss()

                            //After adding we (hide keyboard)

                            Utils.hideKeyboard(this@CategoryDialogFragment!!.context!!, inputText!!)

                        }

                    })

                }
            }
        }

        return v
    }

    fun addColors(position: Int) {
        for (i in color!!.indices) {
            val c = Colors()

            //Name can be hold from String.xml
            //state (Selected) (true|false) is hold from String Array
            c.name = color!![i]
            c.isSelected = if (i == position) true else false

            colors.add(c)
        }
    }

    fun findPosition(c: String): Int {
        var pos = 0
        for (i in color!!.indices) {
            if (c == color!![i]) {
                pos = i
            }
        }
        return pos
    }

    class Colors {
        var name: String? = null
        var isSelected: Boolean = false
    }

    companion object {

        fun getInstance(category: Category, isedit: Boolean): CategoryDialogFragment {

            //This here we initiate new instance of CategoryDialogFragment via DialogFragment
            //So we cannnot use intent in this case
            //So we need declare argument for this Dialog to use
            val fragment = CategoryDialogFragment()

            val bundle = Bundle()

            bundle.putString("HASHID", category.hasIdCategory)

            bundle.putString("TITLE", category.title)

            bundle.putString("COLOR", category.color)

            bundle.putBoolean("ISEDIT", isedit)

            //Supply the (construction arguments) for (this fragment).
            // The arguments supplied here will be (retained) across fragment (destroy) and (creation).
            //This method (cannot) be called) if the fragment is (added)
            // to a FragmentManager and if isStateSaved() would return true.
            fragment.arguments = bundle

            return fragment
        }
    }


}

