package com.e15.alarmnats.utils

import com.e15.alarmnats.data.AppDatabase
import com.e15.alarmnats.data.PomodoroRepository
import com.e15.alarmnats.viewmodel.PomodoroViewModelFactory

object InjectorUtils {

    fun providePomodoroViewModelFactory(): PomodoroViewModelFactory {
        val pomodoroRepository = PomodoroRepository.getInstance(AppDatabase.getInstance()!!.pomodoroDao)
        return PomodoroViewModelFactory(pomodoroRepository!!)
    }

    fun providePomodoroRepository(): PomodoroRepository {
        return PomodoroRepository.getInstance(AppDatabase.getInstance()!!.pomodoroDao)!!
    }


}