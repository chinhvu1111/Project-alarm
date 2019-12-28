package com.e15.alarmnats.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e15.alarmnats.Model.Group
import com.e15.alarmnats.R
import com.e15.alarmnats.adapter.GroupsAdapter
import com.google.android.gms.tasks.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.lang.NullPointerException
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class GroupTask : Fragment,View.OnClickListener {
    lateinit var auth:FirebaseAuth

    lateinit var firebase:FirebaseDatabase

    lateinit var rcListGroup:RecyclerView

    lateinit var rcMygroup:RecyclerView

    lateinit var addGroup:FloatingActionButton

    lateinit var listGroups:ArrayList<Group>

    lateinit var listMygroup:ArrayList<Group>

    lateinit var lnLoadProgressBar:RelativeLayout

    lateinit var lnLoadMyGroup:RelativeLayout

    lateinit var userCurrent:FirebaseUser

    constructor() : super()

    companion object {
        var idCurrentUser:String=""

    }

    var CREATE_GROUP:Int=1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.activity_group_task,container,false)

        auth=FirebaseAuth.getInstance()

        userCurrent=auth.currentUser!!

        if(userCurrent==null){

            AlertDialog.Builder(activity!!, R.style.MyDialogTheme).setMessage("Bạn chưa đăng nhập bạn có muốn chuyển đến khung đăng nhập?"
            ).setPositiveButton("Có", DialogInterface.OnClickListener { dialog, which ->
                var intent=Intent(activity,LoginActivity::class.java)

                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

                startActivity(intent)

            }).setNegativeButton("Không", null).show()

        }

        rcListGroup=view.findViewById(R.id.rcListGroup)

        listGroups= ArrayList<Group>()

        listMygroup= ArrayList()

        rcListGroup.layoutManager=LinearLayoutManager(activity)

        addGroup=view.findViewById(R.id.addGroup)

        rcMygroup=view.findViewById(R.id.rcMygroup)

        rcMygroup.layoutManager=LinearLayoutManager(activity)

        lnLoadProgressBar=view.findViewById(R.id.lnLoadProgressBar)

        lnLoadMyGroup=view.findViewById(R.id.lnLoadMyGroup)

        firebase= FirebaseDatabase.getInstance()

        addGroup.setOnClickListener(this)

        //-------------------------
        var databaseGroup=firebase.getReference("Group")

        var groupAdapter=GroupsAdapter(activity!!,listGroups)

        rcListGroup.adapter=groupAdapter

        rcListGroup.itemAnimator=DefaultItemAnimator()

        rcListGroup.addItemDecoration(DividerItemDecoration(activity!!.applicationContext,DividerItemDecoration.VERTICAL))

        var mygroupAdapter=GroupsAdapter(activity!!.applicationContext,listMygroup)

        rcMygroup.adapter=mygroupAdapter

        //Reference to User path to get groups of current User
        var userDatabase=firebase.getReference("User")

//        var taskGetGroupOfUser:Task<Any?>

//        var dbSource=TaskCompletionSource<Any?>()

//        var task=dbSource.task


        var valueEventListener=object:ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                lnLoadProgressBar.isVisible=true

                listGroups.clear()

                //Getting all group from Realtime database
                for(groupSnapshot in p0.children){

                    var group=groupSnapshot.getValue(Group::class.java)

                    listGroups.add(group!!)

                }

                groupAdapter.notifyDataSetChanged()

                lnLoadProgressBar.visibility=View.GONE

                //Get user has (current email)
                //This query is used to get my group base on current user id
                var querySecond=userDatabase.orderByChild("email").equalTo(userCurrent.email)

                querySecond.addValueEventListener(object:ValueEventListener{

                    override fun onDataChange(p0: DataSnapshot) {

                        lnLoadMyGroup.isVisible=true

                        listMygroup.clear()

                        for(g in p0.children){

                            //This here is not necessary using path(/abc/ad) because if we use path then --> it has format (key:ad,value...)
                            //Child(abc) --> has (children data)
                            var dataSnapShot=g.child("/Group")

                            //Getting all groups of current user
                            var groupKeySnapShot=dataSnapShot.children

                            for(keyGroup in groupKeySnapShot){

                                for(g1 in listGroups){

                                    if(g1.hashId.equals(keyGroup.key)){

                                        listMygroup.add(g1)

                                    }

                                }

                            }

                        }

                        mygroupAdapter.notifyDataSetChanged()

                        lnLoadMyGroup.isVisible=false

                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        }

        databaseGroup.addValueEventListener(valueEventListener)

        return view

    }


    override fun onClick(v: View?) {

        when(v!!.id){

            R.id.addGroup->{

                var intent=Intent(activity,CreatingGroupActivity::class.java)

                var currentUser=auth.currentUser

                try {

                    intent.putExtra("Email",currentUser!!.email)

                }catch (ex:NullPointerException){

                    Toast.makeText(activity!!.applicationContext,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show()

                    return

                }

                startActivityForResult(intent,CREATE_GROUP)

            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode==CREATE_GROUP&&resultCode==Activity.RESULT_OK){



        }else{

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}
