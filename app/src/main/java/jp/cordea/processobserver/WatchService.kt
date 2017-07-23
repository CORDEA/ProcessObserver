package jp.cordea.processobserver

import android.app.ActivityManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.util.*
import kotlin.concurrent.schedule

/**
 * Created by Yoshihiro Tanaka on 2017/07/06.
 */
class WatchService : Service() {

    var isRunningChanged: (Boolean) -> Unit = { }

    override fun onBind(p0: Intent?): IBinder {
        return WatchBinder(this)
    }

    override fun onCreate() {
        super.onCreate()
        val service = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        isRunningChanged(true)

        launch(CommonPool) {
            Timer().schedule(0, WaitMills) {
                service.runningAppProcesses
                        .map { "%d %s".format(it.pid, it.processName) }
                        .joinToString("\n")
                        .apply {
                            write(this)
                        }
            }
        }
    }

    private fun write(text: String) {
        val dir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(dir, FileName)

        if (!dir.exists()) {
            dir.mkdir()
        }

        val t = "------------\n" + Date().toString() + "\n" + text + "\n------------\n"
        if (file.exists()) {
            file.appendText(t)
        } else {
            file.writeText(t)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent = PendingIntent
                .getActivity(this, 0,
                        Intent(this, MainActivity::class.java), 0)
        val notification =
                NotificationCompat
                        .Builder(this)
                        .setContentIntent(pendingIntent)
                        .build()

        startForeground(startId, notification)
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningChanged(false)
    }

    companion object {

        private val WaitMills = 2000L

        val FileName = "process_observer.log"
    }
}