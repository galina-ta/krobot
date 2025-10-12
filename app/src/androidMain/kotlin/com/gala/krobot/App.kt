package com.gala.krobot

import android.app.Application
import com.gala.maze.di.AppHolder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppHolder.instance = this
    }
}
