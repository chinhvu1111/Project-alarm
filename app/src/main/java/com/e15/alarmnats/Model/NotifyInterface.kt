package com.e15.alarmnats.Model

interface NotifyInterface {
    fun onInserted(item:Event)
    fun showExistingDialog(title:String, color:String)
}