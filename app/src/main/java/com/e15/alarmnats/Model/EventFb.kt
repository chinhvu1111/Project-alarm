package com.e15.alarmnats.Model

class EventFb{
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
    var notify: String? = null
    var repeatMode: String? = null
    var repeatCount: String? = null
    var repeatType: String? = null
    var isDone:Int = -1;

    var isUrgent:Boolean = false

    var isImportant:Boolean = false

    var level:String=""

    var remainingTime:Long = 0

    var levelRecusion:Int = 0

    var parentId:String=""

    lateinit var hashIdCategory:String

    //This attributes is item's
    var isEdit: Boolean = false

    var color: String? = null

    var requestCode:Int = 0

    constructor()

    constructor(event:Event){

        hashId=event.hashId
        hashIdUser=event.hashIdUser
        title=event.title
        description=event.description
        place=event.place
        category=event.category
        startTime=event.startTime
        endTime=event.endTime
        date=event.date
        isShow=event.isShow
        enabled=event.enabled
        type=event.type
        notify=event.notify
        repeatMode=event.repeatMode
        repeatCount=event.repeatCount
        repeatType=event.repeatType
        isDone=event.isDone

        isUrgent=event.isUrgent

        isImportant=event.isImportant

        level=event.level

        remainingTime=event.remainingTime

        levelRecusion=event.levelRecusion

        parentId=event.parentId

        hashIdCategory=event.hashIdCategory

        isEdit=event.isEdit

        color=event.color

        requestCode=event.requestCode

    }

    constructor(title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?,levelRecusion:Int){
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

    constructor(title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, enabled: Int, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?,levelRecusion:Int){
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

    constructor(hashId: String, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, enabled: Int, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?, levelRecusion:Int) {
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

    constructor(hashId: String, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?,levelRecusion:Int) {
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

    constructor(hashId: String, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, enabled: Int, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?, isDone: Int, isUrgent: Boolean, isImportant: Boolean, level: String,levelRecusion:Int) {
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

    constructor(hashId: String, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, enabled: Int, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?, isDone: Int, isUrgent: Boolean, isImportant: Boolean, level: String, remainingTime: Long,levelRecusion:Int) {
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

    constructor(level: Int, title: String?, type: Int, levelRecusion: Int, hashIdCategory: String, isEdit: Boolean, color: String?) {
        this.title = title
        this.type = type
        this.levelRecusion = levelRecusion
        this.hashIdCategory = hashIdCategory
        this.isEdit = isEdit
        this.color = color
    }

    //CategoryFragment in (get events base on given Category)
    constructor(hashId:String,level: Int, title: String?, description: String?, place: String?, category: String?, startTime: String?, endTime: String?, date: String?, isShow: Boolean, type: Int, notify: String?, repeatMode: String?, repeatCount: String?, repeatType: String?, levelRecusion: Int, color: String?){
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

    constructor(level:Int,title: String, color: String, type: Int, isEdit: Boolean,levelRecusion:Int){
        this.title = title
        this.color = color
        this.type = type
        this.isEdit = isEdit
    }


    companion object {

        val EVENT_TYPE = 1

    }
}