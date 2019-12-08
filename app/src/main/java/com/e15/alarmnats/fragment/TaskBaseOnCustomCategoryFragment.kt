package com.e15.alarmnats.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Database.ReminderDatabase
import com.e15.alarmnats.Model.Category
import com.e15.alarmnats.Model.Event
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.CategoryTaskEisAdapter

class TaskBaseOnCustomCategoryFragment: Fragment() {

    companion object {

        lateinit var recyclelistCategories: RecyclerView;

        lateinit var context: Context

        lateinit var childFragmentManagers: FragmentManager

        lateinit var edtFilter: EditText

    }

//    lateinit var listEvent: MutableLiveData<ArrayList<Event>>
//    lateinit var listEvent1: MutableLiveData<ArrayList<Event>>
//    lateinit var listEvent2: MutableLiveData<ArrayList<Event>>
//    lateinit var listEvent3: MutableLiveData<ArrayList<Event>>

    lateinit var listCategory:ArrayList<Category>;

    lateinit var mdbHandler: ReminderDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //get to add fragment outside of
        // creatView() function of the current fragment


//        listEvent.add(Event(2, "sdfsd", "sdfsd", "sdfsd", "", "", "", "", false, 2, 3, "", "", "", ""))

        listCategory= ArrayList()

        mdbHandler=ReminderDatabase(activity!!.applicationContext)

//        listEvent.value=mdbHandler.getEvents(0,0)
//        listEvent1.value=mdbHandler.getEvents(0,1)
//        listEvent2.value=mdbHandler.getEvents(1,0)
//        listEvent3.value=mdbHandler.getEvents(1,1)

        if(ActionBottomDialogFragment.choice==1){

            var categories=resources.getStringArray(R.array.systemCategory)

            var category:Category=Category("1",categories.get(0))
            var category1:Category=Category("2",categories.get(1))
            var category2:Category=Category("3",categories.get(2))
            var category3:Category=Category("4",categories.get(3))
            var category4:Category= Category("5",categories.get(4))

            listCategory.add(category)
            listCategory.add(category1)
            listCategory.add(category2)
            listCategory.add(category3)
            listCategory.add(category4)

        }else{

            listCategory.addAll(mdbHandler.allCategory)

        }

        var view=inflater.inflate(R.layout.fragment_tasks_base_on_category, container, false)

        edtFilter=view.findViewById(R.id.edtFilterName)

        recyclelistCategories=view.findViewById(R.id.recyclelistCategories);

        var categoryTaskEisAdapter=CategoryTaskEisAdapter()

        var tempRs= ArrayList(listCategory)

        categoryTaskEisAdapter.listCategory=tempRs

        categoryTaskEisAdapter.contexts=context!!

        recyclelistCategories.adapter=categoryTaskEisAdapter

        recyclelistCategories.itemAnimator= DefaultItemAnimator()

        recyclelistCategories.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        recyclelistCategories.layoutManager= LinearLayoutManager(context)

        childFragmentManagers=childFragmentManager

        edtFilter.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                tempRs.clear()

                var re=Regex("[^A-Za-z0-9 ]")

                var strSearch=re.replace(edtFilter.text.toString().toLowerCase(),"")

//                strSearch=strSearch.replace("/[!@#\$%^&*]/g","")
//                strSearch=strSearch.replace("s/([()])//g","")

                for(category in listCategory){

                    var regex=".*("+strSearch.toLowerCase()+").*"

                    if(category.title.toLowerCase().matches(regex = Regex(regex))){

                        tempRs.add(category)

                    }

                }

//                listCategory.addAll(tempRs)

                categoryTaskEisAdapter.notifyDataSetChanged()

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })

        return view

    }

//    fun getList(): LiveData<ArrayList<Event>> {
//
//        var result= MutableLiveData<ArrayList<Event>>()
//
//        result=listEvent
//
//        return result
//
//    }
//    fun getList1(): LiveData<ArrayList<Event>> {
//
//        var result= MutableLiveData<ArrayList<Event>>()
//
//        result=listEvent1
//
//        return result
//
//    }
//    fun getList2(): LiveData<ArrayList<Event>> {
//
//        var result= MutableLiveData<ArrayList<Event>>()
//
//        result=listEvent2
//
//        return result
//
//    }
//    fun getList3(): LiveData<ArrayList<Event>> {
//
//        var result= MutableLiveData<ArrayList<Event>>()
//
//        result=listEvent3
//
//        return result
//
//    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        AsyncTaskActivity().execute()
//    }

    class AsyncTaskActivity: AsyncTask<Void, Integer, String>() {

        override fun doInBackground(vararg params: Void?): String {

            publishProgress(Integer(1))

            return "done"

        }

        override fun onProgressUpdate(vararg values: Integer?) {

            edtFilter.isVisible=false

            DetailSettingTaskEIS.contexts= context

            childFragmentManagers.beginTransaction().add(
                    R.id.settingDetail,
                    DetailSettingTaskEIS()
            )
                    .commit()
        }

    }

}

//class ViewModelFilter{
//
//    val query=MutableLiveData<String>()
//
//    val categoryResult=LiveData<List<Category>>
//
//    fun filterCategoryByName(name:String)= apply { query.value=name }
//
//}