package jp.cordea.processobserver

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.tintedButton
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File

class MainActivity : AppCompatActivity() {

    private var service: WatchService? = null
        set(value) {
            value?.let {
                it.isRunningChanged = isRunningChanged
            }
        }

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@MainActivity.service = (service as WatchBinder).service
            isRunningChanged(true)
        }
    }

    var isRunningChanged: (Boolean) -> Unit = { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityUi().setContentView(this)
    }

    fun removeLogFile() {
        val dir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(dir, WatchService.FileName)
        if (file.exists()) {
            file.delete()
        }
    }

    class MainActivityUi : AnkoComponent<MainActivity> {

        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            frameLayout {
                padding = dip(16)

                textView(R.string.stopped_text) {
                    owner.isRunningChanged = {
                        textResource =
                                if (it) R.string.running_text
                                else R.string.stopped_text
                    }
                    textSize = 56f
                }.lparams {
                    gravity = Gravity.CENTER
                    bottomMargin = dip(32)
                }

                verticalLayout {
                    gravity = Gravity.BOTTOM
                    tintedButton(R.string.start_button) {
                        onClick {
                            startService<WatchService>()
                            bindService<WatchService>(owner.connection)
                        }
                        lparams {
                            width = matchParent
                            bottomMargin = dip(8)
                        }
                    }
                    tintedButton(R.string.stop_button) {
                        onClick {
                            stopService<WatchService>()
                            unbindService(owner.connection)
                        }
                        lparams {
                            width = matchParent
                            bottomMargin = dip(8)
                        }
                    }
                    tintedButton(R.string.remove_button) {
                        onClick {
                            owner.removeLogFile()
                        }
                        lparams {
                            width = matchParent
                        }
                    }
                }
            }
        }
    }
}
