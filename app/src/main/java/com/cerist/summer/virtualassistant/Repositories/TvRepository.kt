package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Utils.Data.Status
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executor

class TvRepository(private val broadLinkRepository: BroadLinkRepository,
                   private val bluetoothExecutor: Executor) : IRepository {

    companion object {
        private val TAG = "TvRepository"
    }

     val broadLinkConnection: Observable<RxBleConnection>
     val broadLinkConnectionState:Observable<RxBleConnection.RxBleConnectionState>


    init {

        broadLinkConnection = broadLinkRepository.broadLinkConnection
        broadLinkConnectionState =  broadLinkRepository.broadLinkConnectionState

    }



    fun getTvPowerState(bleConnection: RxBleConnection)
       =  bleConnection.readCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID))
            .toObservable()
            .flatMap {
                Observable.just(it.toString(Charset.defaultCharset()).toInt()) }
            .flatMap {
                when (it) {
                    0 -> Observable.just(BroadLinkProfile.TvProfile.State.OFF)
                    1 ->  Observable.just(BroadLinkProfile.TvProfile.State.ON)
                    else -> Observable.error(Throwable(Status.OPERATION_ERROR))
                }}
            .share()!!


    fun getTvVolume(bleConnection: RxBleConnection)
       = bleConnection.readCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.VOLUME_CHARACTERISTIC_UUID))
            .toObservable()
            .flatMap {
                Observable.just(it.toString(Charset.defaultCharset()).toInt())}
            .flatMap {
                if(it in BroadLinkProfile.TvProfile.MIN_VOLUME
                        ..BroadLinkProfile.TvProfile.MAX_VOLUME)
                    Observable.just(it)
                else
                    Observable.error(Throwable(Status.OPERATION_ERROR))}
            .share()!!



    fun setTvPowerState(bleConnection: RxBleConnection,state: BroadLinkProfile.TvProfile.State)
            = bleConnection.writeCharacteristic(
                                UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID),
                                byteArrayOf(state.value.toByte()))
                    .toObservable()
                    .flatMap {
                        Observable.just(it[0].toInt()) }
                    .flatMap {
                            when (it) {
                                0 -> Observable.just(BroadLinkProfile.TvProfile.State.OFF)
                                1 ->  Observable.just(BroadLinkProfile.TvProfile.State.ON)
                                else -> Observable.error(Throwable(Status.OPERATION_ERROR))
                            }
                        }
                    .share()!!


    fun setTvVolumeLevel(bleConnection: RxBleConnection,volume:Int)
          = Observable.just(bleConnection)
                .flatMap {
                    if(volume in BroadLinkProfile.TvProfile.MIN_VOLUME
                                        .. BroadLinkProfile.TvProfile.MAX_VOLUME)
                        Observable.just(it)
                    else
                        Observable.error(Throwable(Status.OPERATION_ERROR))
                 }
                .flatMap {
                    getTvVolume(it) }
                .flatMap {
                    Log.d(TAG,"Writing the tv volume level characteristic")
                    if(volume > it)
                        bleConnection.writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.VOLUME_UP_CHARACTERISTIC_UUID),
                            byteArrayOf(volume.toByte())).toObservable()
                    else
                        bleConnection.writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.VOLUME_DOWN_CHARACTERISTIC_UUID),
                                byteArrayOf(volume.toByte())).toObservable()
                                }
                .flatMap {
                    Observable.just(it[0].toInt()) }
                .flatMap {
                    Observable.just(it) }
                .share()!!


    fun setTvTimer(bleConnection: RxBleConnection,time:Int)
        =Observable.just(bleConnection)
            .flatMap {
                if(time in BroadLinkProfile.TvProfile.TV_TIMER_SET)
                    Observable.just(it)
                else
                    Observable.error(Throwable(Status.OPERATION_ERROR)) }
            .flatMap {
                Log.d(TAG,"Writing the tv timer  characteristic")

                    bleConnection.writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.TIMER_CHARACTERISTIC_UUID),
                            byteArrayOf(time.toByte())).toObservable()
            }
            .flatMap {
                Observable.just(it[0].toInt()) }
            .flatMap {
                Observable.just(it) }
            .share()!!

}