package com.example.timesnewswire.core

import android.app.Application

class App: Application() {
    private var coreSingleton: CoreSingleton = CoreSingleton.getInstance(this)
}