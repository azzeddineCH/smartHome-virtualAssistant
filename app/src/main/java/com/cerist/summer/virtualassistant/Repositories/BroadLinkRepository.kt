package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class BroadLinkRepository ( private val broadLink: Observable<RxBleDevice>,
                          private val bluetoothExecutor: Executor) : IRepository {
    companion object {
        private val TAG = "BroadLinkRepository"
    }

    val broadLinkConnection: Observable<RxBleConnection>
    val broadLinkConnectionState: Observable<RxBleConnection.RxBleConnectionState>

    init {
        broadLinkConnection = broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    Log.d(TAG, "connecting the broadLink GATT server")
                    it.establishConnection(true)
                }
                .retry()
                .share()

        broadLinkConnectionState = broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    Log.d(TAG, "Observing the BroadLink GATT server connection state")
                    it.observeConnectionStateChanges()
                }
                .share()
    }

}