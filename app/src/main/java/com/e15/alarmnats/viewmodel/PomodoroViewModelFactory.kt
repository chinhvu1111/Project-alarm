package com.e15.alarmnats.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e15.alarmnats.data.PomodoroRepository

class PomodoroViewModelFactory(private val pomodoroRepository: PomodoroRepository) :
//ViewModelProvider
//An utility class that provides (ViewModels) for a scope.
//Default ViewModelProvider for an Activity or a Fragment can be obtained from ViewModelProviders class.

//NewInstanceFactory()
//Simple factory, which calls empty constructor on the give class.
        ViewModelProvider.NewInstanceFactory() {

    //(Instances) of (the class Class) represent (classes) and (interfaces) in a (running Java application)
    @SuppressLint("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PomodoroViewModel(pomodoroRepository) as T
    }
}