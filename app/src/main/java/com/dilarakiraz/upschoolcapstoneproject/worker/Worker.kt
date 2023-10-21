package com.dilarakiraz.upschoolcapstoneproject.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dilarakiraz.upschoolcapstoneproject.utilities.NotificationUtils

/**
 * Created on 20.10.2023
 * @author Dilara Kiraz
 */

class Worker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        NotificationUtils.showNotification(applicationContext)
        return Result.success()
    }
}