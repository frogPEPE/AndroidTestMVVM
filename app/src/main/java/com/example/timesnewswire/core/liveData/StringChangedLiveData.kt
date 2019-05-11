package com.example.timesnewswire.core.liveData

import androidx.lifecycle.LiveData

class StringChangedLiveData: LiveData<String>() {
    fun SetNewString(newString: String){
        postValue(newString)
    }
}