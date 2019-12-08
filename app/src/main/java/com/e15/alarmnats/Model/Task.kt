package com.e15.alarmnats.Model

import android.content.Intent

class Task :Event{
    constructor(levelRecusion: Int) : super(levelRecusion)


    fun toIntent(intent: Intent){

        intent.putExtra("id",hashId);
        intent.putExtra("date",date);
        intent.putExtra("startTime",startTime);
        intent.putExtra("endTime",endTime);
        intent.putExtra("title",title)
        intent.putExtra("description",description);
        intent.putExtra("enabled",enabled);

        intent.putExtra("taskIsDone",isDone)
        intent.putExtra("remainingTime",remainingTime)

    }

    fun fromIntent(intent: Intent){

        hashId=if(intent.getStringExtra("id")==null) "" else intent.getStringExtra("id");
        date=intent.getStringExtra("date");
        startTime=intent.getStringExtra("startTime");
        endTime=intent.getStringExtra("endTime")

        description=intent.getStringExtra("label");
        title=intent.getStringExtra("title")
        enabled=intent.getIntExtra("enabled",enabled);
        isDone=intent.getIntExtra("taskIsDone",0)
        remainingTime=intent.getLongExtra("remainingTime",0)

    }

}