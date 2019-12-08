package com.e15.alarmnats.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.e15.alarmnats.Model.Task

class DBTaskManager : SQLiteOpenHelper {

    lateinit var context: Context;

    val DATABASE_NAME="Task_managers";

    val TABLE_NAME="Task";

    var ID="id";

    var DATE="DATE";

    var TIME="TIME";

    var LABEL="DESCRIPTION";

    var SQLQuery="CREATE TABLE "+TABLE_NAME+" ("+
            ID+" integer primary key,"+
            DATE+" TEXT,"+
            TIME+" TEXT,"+
            LABEL+" TEXT)";

    var SQLSELECTION="SELECT * FROM "+TABLE_NAME;

    constructor(
            context: Context?,
            name: String?,
            factory: SQLiteDatabase.CursorFactory?,
            version: Int,
            context1: Context
    ) : super(context, "Task_managers", null, 1) {
        this.context = context1
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQLQuery);
    }

    fun addTask(task:Task){

        var database: SQLiteDatabase =this.writableDatabase;

        var contentValues: ContentValues = ContentValues();

        contentValues.put(DATE,task.date);
        contentValues.put(TIME,task.startTime);
        contentValues.put(LABEL,task.description);

        database.insert(TABLE_NAME,null,contentValues);

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun getTaskByInfo():List<Task>{

        var database: SQLiteDatabase =this.readableDatabase;

        var taskList=ArrayList<Task>();

        var cursor: Cursor =database.rawQuery(SQLSELECTION,null);

        if(cursor.moveToFirst()){
            do{
                var task:Task= Task(0);

                task.date= cursor.getInt(1).toString();

                task.startTime=cursor.getInt(2).toString();

                task.startTime=cursor.getInt(3).toString();

                taskList.add(task);

            }while (cursor.moveToFirst())
        }

        return taskList;

    }


}