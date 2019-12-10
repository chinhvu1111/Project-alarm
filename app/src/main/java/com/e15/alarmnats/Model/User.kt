package com.e15.alarmnats.Model

class User {

    var username:String=""

    var description:String=""

    var hashId:String

    var email:String

    var password:String=""

    constructor(hashId: String, email: String) {
        this.hashId = hashId
        this.email = email
    }

    constructor(username: String, description: String, hashId: String, email: String,password:String) {
        this.username = username
        this.description = description
        this.hashId = hashId
        this.email = email
        this.password = password
    }


}