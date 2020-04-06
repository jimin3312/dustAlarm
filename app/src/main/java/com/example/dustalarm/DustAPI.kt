package com.example.dustalarm

import java.io.BufferedReader
import java.net.HttpURLConnection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class DustAPI {
    lateinit var service: ExecutorService
    lateinit var bufferedReader: BufferedReader
    lateinit var connection: HttpURLConnection
    constructor(){
        service = Executors.newSingleThreadExecutor()
    }
}