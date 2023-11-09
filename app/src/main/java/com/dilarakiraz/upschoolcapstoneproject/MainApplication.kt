package com.dilarakiraz.upschoolcapstoneproject

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dilarakiraz.upschoolcapstoneproject.worker.Worker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MainApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        val workRequest = PeriodicWorkRequestBuilder<Worker>(
            1,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}