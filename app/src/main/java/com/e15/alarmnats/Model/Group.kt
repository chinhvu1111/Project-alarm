package com.e15.alarmnats.Model

class Group {

    lateinit var hashId:String

    lateinit var name:String

    var numberOfMember:Int = 0

    lateinit var path:String

    var hashIdAdmin:String=""

    constructor()

    constructor(hashId: String, name: String, numberOfMember: Int, path: String) {
        this.hashId = hashId
        this.name = name
        this.numberOfMember = numberOfMember
        this.path = path
    }

    constructor(hashId: String, name: String, numberOfMember: Int, path: String, hashIdAdmin: String) {
        this.hashId = hashId
        this.name = name
        this.numberOfMember = numberOfMember
        this.path = path
        this.hashIdAdmin = hashIdAdmin
    }


}