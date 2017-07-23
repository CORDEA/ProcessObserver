package jp.cordea.processobserver

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import org.jetbrains.anko.AnkoContext

/**
 * Created by Yoshihiro Tanaka on 2017/07/21.
 */
inline fun <reified T : Service> AnkoContext<*>.bindService(connection: ServiceConnection) {
    this.ctx.bindService(Intent(this.ctx, T::class.java), connection, 0)
}

fun AnkoContext<*>.unbindService(connection: ServiceConnection) {
    this.ctx.unbindService(connection)
}

inline fun <reified T : Service> AnkoContext<*>.stopService() {
    this.ctx.stopService(Intent(this.ctx, T::class.java))
}
