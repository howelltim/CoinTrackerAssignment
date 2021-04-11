package ca.burchill.cointracker.work

import android.content.Context
import androidx.work.*
import ca.burchill.cointracker.database.getDatabase
import ca.burchill.cointracker.repository.CoinsRepository
import retrofit2.HttpException
import timber.log.Timber

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "com.example.android.devbyteviewer.work.RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = CoinsRepository(database)

        try {
            Timber.d("Work request for sync is run")
            repository.refreshCoins()
        } catch (e: HttpException) {
            return Result.retry()
        }

        return Result.success()
    }
}