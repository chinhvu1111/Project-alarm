package com.e15.alarmnats.Model

class Category {

    companion object {
        val CATEGORY_TYPE = 0
    }

    var hasIdCategory:String=""

    var title: String="";

    var color:String="";

    var type:Int = 0;

    var hashIdUser:String=""

    constructor()

    constructor(hasIdCategory: String, title: String, color: String, type: Int) {
        this.hasIdCategory = hasIdCategory
        this.title = title
        this.color = color
        this.type = type
    }

    constructor(hasIdCategory: String, title: String) {
        this.hasIdCategory = hasIdCategory
        this.title = title
    }

    constructor(hasIdCategory: String, title: String, hashIdUser: String) {
        this.hasIdCategory = hasIdCategory
        this.title = title
        this.hashIdUser = hashIdUser
    }

    constructor(hasIdCategory: String, title: String, color: String, hashIdUser: String) {
        this.hasIdCategory = hasIdCategory
        this.title = title
        this.color = color
        this.hashIdUser = hashIdUser
    }


}