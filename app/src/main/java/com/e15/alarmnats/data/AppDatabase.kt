package com.e15.alarmnats.data

class AppDatabase private constructor() {
    var pomodoroDao: PomodoroDao

    init {
        pomodoroDao = PomodoroDao()
    }

    companion object {

        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (instance == null) {
                        instance = AppDatabase()
                    }
                }
            }
            return instance
        }
    }
}