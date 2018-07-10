package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executor

class TvRepository(private val broadLink: Observable<RxBleDevice>,
                   private val bluetoothExecutor: Executor) : IRepository {

    companion object {
        private val TAG = "TvRepository"
    }

     val broadLinkConnection: Observable<RxBleConnection>
     val broadLinkConnectionState:Observable<RxBleConnection.RxBleConnectionState>

     val tvPowerState:Observable<BroadLinkProfile.TvProfile.State>
     val tvVolumeLevel:Observable<Int>

    init {

        broadLinkConnection =  broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(TAG,"connecting to the GATT server named ${it.name}")
                    it.establishConnection(true)
                }
                .retry()
                .share()

        broadLinkConnectionState =  broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(TAG,"start to observe the connection state with ${it.name}: ${it.connectionState.name}")
                    it.observeConnectionStateChanges()
                }
                .share()

        tvPowerState =   broadLinkConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(TAG,"reading from the Characteristic")
                    it.readCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID))
                            .toObservable()}
                .flatMap {
                    Observable.just(it[0].toInt()) }
                .flatMap { value ->
                        when (value) {
                            0 -> Observable.just(BroadLinkProfile.TvProfile.State.OFF)
                            1 ->  Observable.just(BroadLinkProfile.TvProfile.State.ON)
                            else -> Observable.error(Throwable("unknown value"))
                        }
                    }
                .share()

        tvVolumeLevel =  broadLinkConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(TAG,"reading from the Characteristic")
                    it.readCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID))
                            .toObservable()}
                .flatMap { Observable.just(it[0].toInt()) }
                .flatMap {
                      if(it in BroadLinkProfile.TvProfile.MIN_VOLUME
                                           ..BroadLinkProfile.TvProfile.MAX_VOLUME)
                          Observable.just(it)
                            else
                           Observable.error(Throwable("inappropriate value"))
                    }
                .share()


    }

    fun setTvPowerState(state: BroadLinkProfile.TvProfile.State)
            = broadLinkConnection
                    .observeOn(Schedulers.from(bluetoothExecutor))
                    .flatMap {
                        it.writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID),
                                byteArrayOf(state.value.toByte())).toObservable()
                    }
                    .flatMap {
                        Observable.just(it[0].toInt()) }
                    .flatMap {
                            when (it) {
                                0 -> Observable.just(BroadLinkProfile.TvProfile.State.OFF)
                                1 ->  Observable.just(BroadLinkProfile.TvProfile.State.ON)
                                else -> Observable.error(Throwable("unknown value"))
                            }
                        }
                    .share()!!


    fun setTvVolumeLevel(volume:Int)
          = broadLinkConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    if(volume in BroadLinkProfile.TvProfile.MIN_VOLUME
                                        .. BroadLinkProfile.TvProfile.MAX_VOLUME)
                        Observable.just(it)
                    else
                         Observable.error(Throwable("inappropriate value"))
                 }
                .flatMap {
                    tvVolumeLevel }
                .flatMap {
                    if(volume > it)
                        broadLinkConnection.blockingLast().writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.VOLUME_UP_CHARACTERISTIC_UUID),
                            byteArrayOf(volume.toByte())).toObservable()
                    else
                        broadLinkConnection.blockingLast().writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.VOLUME_DOWN_CHARACTERISTIC_UUID),
                                byteArrayOf(volume.toByte())).toObservable() }
                .flatMap {
                    Observable.just(it[0].toInt()) }
                .flatMap {
                    Observable.just(it) }
                .share()!!
}