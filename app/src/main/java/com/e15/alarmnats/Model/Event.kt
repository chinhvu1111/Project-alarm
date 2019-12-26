package com.e15.alarmnats.Model

import com.multilevelview.models.RecyclerViewItem
import java.io.Serializable

open class Event:RecyclerViewItem {

    var hashId:String=""
    var hashIdUser:String=""
    var title: String? = null
    var description: String? = null
    var place: String? = null
    var category: String? = null
    var startTime: String? = null
    var endTime:String?=null;
    var date: String? = null
    var isShow: Boolean = false
    var enabled:Int = 0;
    var type: Int = 0

    //This attribute not necessary in database
    var notify: String? = null
    var repeatMode: String? = null
    var repeatCount: String? = null
    var repeatType: String? = null
    var isDone:Int = -1;

    var isUrgent:Boolean = false

    var isImportant:Boolean = false

    //This is not necessary --> not format 3NF
    var level:String=""

    var remainingTime:Long = 0

    var levelRecusion:Int = 0

    var parentId:String=""

    var hashIdCategory:String=""

    //This attributes is item's
    var isEdit: Boolean = false

    var color: String? = null

    var requestCode:Int = 0

    constructor(levelRecusion:Int):super(levelRecusion) {

        this.levelRecusion=levelRecusion

    }

    constructor(eventFb: EventFb,levelRecusion: Int):super(levelRecusion){
        hashId=eventFb.hashId
        hashIdUser=eventFb.hashIdUser
        title=eventFb.title
        description=eventFb.description
        place=eventFb.place
        category=eventFb.category
        startTime=eventFb.startTime
        endTime=eventFb.endTime
        date=eventFb.date

        isShow=eventFb.isShow

        enabled=eventFb.enabled

        type=eventFb.type

        notify=eventFb.notify

        repeatMode=eventFb.repeatMode

        repeatCount=eventFb.repeatCount
        repeatType=eventFb.repeatType
        isDone=eventFb.isDone

        isUrgent=eventFb.isUrgent

        isImportant=eventFb.isImportant

        level=eventFb.level

        remainingTime=eventFb.remainingTime

        this.levelRecusion=eventFb.levelRecusion

        parentId=eventFb.parentId

        hashIdCategory=eventFb.hashIdCategory

        isEdit=eventFb.isEdit

        color=eventFb.color

        requestCode=eventFb.requestCode
    }

    constructor(title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?,levelRecusion:Int) :super(levelRecusion){
        this.title = title
        this.description = description
        this.place = place
        this.category = category
        this.startTime = startTime
        this.endTime = endTime
        this.date = date
        this.type = type
        this.notify = notify
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.repeatType = repeatType
        this.levelRecusion=levelRecusion
    }

    constructor(title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, enabled: Int, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?,levelRecusion:Int) :super(levelRecusion){
        this.title = title
        this.description = description
        this.place = place
        this.category = category
        this.startTime = startTime
        this.endTime = endTime
        this.date = date
        this.enabled = enabled
        this.type = type
        this.notify = notify
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.repeatType = repeatType
        this.levelRecusion=levelRecusion
    }

    constructor(hashId: String, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, enabled: Int, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?, levelRecusion:Int):super(levelRecusion) {
        this.hashId = hashId
        this.title = title
        this.description = description
        this.place = place
        this.category = category
        this.startTime = startTime
        this.endTime = endTime
        this.date = date
        this.isShow = isShow
        this.enabled = enabled
        this.type = type
        this.notify = notify
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.repeatType = repeatType
        this.levelRecusion=levelRecusion
    }

    constructor(hashId: String, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?,levelRecusion:Int):super(levelRecusion) {
        this.hashId = hashId
        this.title = title
        this.description = description
        this.place = place
        this.category = category
        this.startTime = startTime
        this.endTime = endTime
        this.date = date
        this.isShow = isShow
        this.type = type
        this.notify = notify
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.repeatType = repeatType
        this.levelRecusion=levelRecusion
    }

    constructor(hashId: String, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, enabled: Int, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?, isDone: Int, isUrgent: Boolean, isImportant: Boolean, level: String,levelRecusion:Int):super(levelRecusion) {
        this.hashId = hashId
        this.title = title
        this.description = description
        this.place = place
        this.category = category
        this.startTime = startTime
        this.endTime = endTime
        this.date = date
        this.isShow = isShow
        this.enabled = enabled
        this.type = type
        this.notify = notify
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.repeatType = repeatType
        this.isDone = isDone
        this.isUrgent = isUrgent
        this.isImportant = isImportant
        this.level = level
        this.levelRecusion=levelRecusion
    }

    constructor(hashId: String, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, enabled: Int, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?, isDone: Int, isUrgent: Boolean, isImportant: Boolean, level: String, remainingTime: Long,levelRecusion:Int) :super(levelRecusion){
        this.hashId = hashId
        this.title = title
        this.description = description
        this.place = place
        this.category = category
        this.startTime = startTime
        this.endTime = endTime
        this.date = date
        this.isShow = isShow
        this.enabled = enabled
        this.type = type
        this.notify = notify
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.repeatType = repeatType
        this.isDone = isDone
        this.isUrgent = isUrgent
        this.isImportant = isImportant
        this.level = level
        this.remainingTime = remainingTime
        this.levelRecusion=levelRecusion
    }

    constructor(level: Int, title: String?, type: Int, levelRecusion: Int, hashIdCategory: String, isEdit: Boolean, color: String?) : super(level) {
        this.title = title
        this.type = type
        this.levelRecusion = levelRecusion
        this.hashIdCategory = hashIdCategory
        this.isEdit = isEdit
        this.color = color
    }

    //CategoryFragment in (get events base on given Category)
    constructor(hashId:String,level: Int, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?, levelRecusion: Int, color: String?) : super(levelRecusion) {
        this.hashId=hashId
        this.title = title
        this.description = description
        this.place = place
        this.category = category
        this.startTime = startTime
        this.endTime = endTime
        this.date = date
        this.isShow = isShow
        this.type = type
        this.notify = notify
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.repeatType = repeatType
        this.levelRecusion = levelRecusion
        this.color = color
    }

    constructor(level:Int,title: String, color: String, type: Int, isEdit: Boolean,levelRecusion:Int):super(level){
        this.title = title
        this.color = color
        this.type = type
        this.isEdit = isEdit
    }

    companion object {

        val EVENT_TYPE = 1
    }
}