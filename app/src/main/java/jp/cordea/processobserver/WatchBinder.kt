package jp.cordea.processobserver

import android.os.Binder

/**
 * Created by Yoshihiro Tanaka on 2017/07/21.
 */
class WatchBinder(val service: WatchService) : Binder()
