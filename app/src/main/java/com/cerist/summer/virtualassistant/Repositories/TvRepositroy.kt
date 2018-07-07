package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Utils.Resource
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executor

class TvRepositroy(val broadLink: Observable<RxBleDevice>,
                   val bluetoothExecutor: Executor) : IRepository {

    companion object {
        val TAG = "TvRepositroy"
    }

    private val  mBroadLinkConnection: ConnectableObservable<RxBleConnection>
    private val  mBroadLinkConnectionState:Observable<RxBleConnection.RxBleConnectionState>

    private val  mTvPowerState:Observable<Resource<BroadLinkProfile.TvProfile.TV_STATE>>
    private val  mTvVolumeLevel:Observable<Resource<Int>>

    init {
        mBroadLinkConnection =  broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"connecting to the GATT server named ${it.name}")
                    it.establishConnection(false)
                }.publish()

        mBroadLinkConnectionState =  broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"start to observe the connection state with ${it.name}: ${it.connectionState.name}")
                    it.observeConnectionStateChanges()
                }


        mTvPowerState =   mBroadLinkConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"reading from the Characteristic")
                    it.readCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID))
                            .toObservable()}
                .flatMap { bytes ->
                    Observable.just(bytes[0].toInt()) }
                .flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<BroadLinkProfile.TvProfile.TV_STATE>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(BroadLinkProfile.TvProfile.TV_STATE.OFF))
                            1 ->  e.onNext(Resource.success(BroadLinkProfile.TvProfile.TV_STATE.ON))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }}
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }


        mTvVolumeLevel =  mBroadLinkConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"reading from the Characteristic")
                    it.readCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID))
                            .toObservable()}
                .flatMap { bytes ->
                    Observable.just(bytes[0].toInt()) }
                .flatMap { volume ->
                    Observable.create { e: ObservableEmitter<Resource<Int>> ->
                      if(volume in BroadLinkProfile.TvProfile.minVolume
                                           ..BroadLinkProfile.TvProfile.maxVolume)
                          e.onNext(Resource.success(volume))
                            else
                          e.onNext(Resource.error("unknown value",null))
                        }
                    }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null) }

    }

    fun getTvConnectionState() = mBroadLinkConnectionState
    fun getTvPowerState() = mTvPowerState
    fun getTvVolumLevel() = mTvVolumeLevel

    fun setTvPowerState(state:BroadLinkProfile.TvProfile.TV_STATE)
                    :Observable<Resource<BroadLinkProfile.TvProfile.TV_STATE>>{
        val i: Byte = when (state) {
            BroadLinkProfile.TvProfile.TV_STATE.OFF -> 0
            BroadLinkProfile.TvProfile.TV_STATE.ON -> 1
        }
        return mBroadLinkConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID),
                            byteArrayOf(i)).toObservable()
                }.flatMap { bytes ->
                    Observable.create { e: ObservableEmitter<Int> ->
                        e.onNext(bytes[0].toInt())
                    }
                }.flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<BroadLinkProfile.TvProfile.TV_STATE>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(BroadLinkProfile.TvProfile.TV_STATE.OFF))
                            1 ->  e.onNext(Resource.success(BroadLinkProfile.TvProfile.TV_STATE.ON))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }
                }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }

    }

    fun setTvVolumeLevel(volume:Byte)
            :Observable<Resource<Int>>{

            if(volume !in BroadLinkProfile.TvProfile.minVolume ..
                    BroadLinkProfile.TvProfile.maxVolume)
                return Observable.just(
                    Resource.error("inappropriate value",null)
            )
        return mBroadLinkConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.VOLUME_CHARACTERISTIC_UUID),
                            byteArrayOf(volume)).toObservable()
                }.flatMap { bytes ->
                    Observable.create { e: ObservableEmitter<Int> ->
                        e.onNext(bytes[0].toInt())
                    }
                }.flatMap { volume ->
                    Observable.create { e: ObservableEmitter<Resource<Int>> ->
                        if(volume in BroadLinkProfile.TvProfile.minVolume
                                ..BroadLinkProfile.TvProfile.maxVolume)
                            e.onNext(Resource.success(volume))
                        else
                            e.onNext(Resource.error("unknown value",null))
                    }
                }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }
    }
}