package com.example.timesnewswire.volley

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context: Context) {
    companion object {
        @Synchronized
        fun getInstance(context: Context): VolleySingleton {
            if (!Companion::instance.isInitialized)
                instance = VolleySingleton(context)

            return instance
        }

        private lateinit var instance: VolleySingleton
        fun isInstanceInit() = Companion::instance.isInitialized
    }

    init {
        //Prevent form the reflection api.
        if (isInstanceInit()){
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
    }

    private val appContext: Context by lazy { context.applicationContext }
    val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(context.applicationContext) }
}