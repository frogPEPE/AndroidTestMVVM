package com.example.timesnewswire.core.liveData

interface LiveDataOnChangedListener <T> {
    fun OnChanged(data: List<T>)
}