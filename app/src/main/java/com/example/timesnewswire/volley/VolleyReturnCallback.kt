package com.example.timesnewswire.volley

interface VolleyReturnCallback {
    fun onResponse(result: String)
    fun onError(error: String)
}