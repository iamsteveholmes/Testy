package com.example.testy

import kotlinx.coroutines.withContext

class Greeting {
    fun greeting(): String {
        withContext {

        }
        return "Hello, ${Platform().platform}!"
    }
}