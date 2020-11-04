package com.basemvp.main.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.base.common.util.log
import kotlinx.coroutines.delay

class MainWork(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        delay(2000)
        setProgress(Data.Builder().putInt("int", 10).build())
        delay(3000)
        setProgress(Data.Builder().putInt("int", 80).build())
        log("MainWork", "doWork")
        delay(100)
        return Result.success()
    }
}