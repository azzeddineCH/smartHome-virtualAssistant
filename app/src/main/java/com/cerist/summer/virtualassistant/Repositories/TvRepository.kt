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

class TvRepository(val broadLink: Observable<RxBleDevice>,
                   val bluetoothExecutor: Executor) : IRepository {

    companion object {
        val TAG = "TvRepository"
    }

    private val  mBroadLinkConnection: ConnectableObservable<RxBleConnection>
    private val  mBroadLinkConnectionState:Observable<RxBleConnection.RxBleConnectionState>

    private val  mState:Observable<Resource<BroadLinkProfile.TvProfile.State>>
    private val  mTvVolumeLevel:Observable<Resource<Int>>

    init {
        mBroadLinkConnection =  broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"connecting to the GATT server named ${it.name}")
                    it.establishConnection(false)
                }
                .publish()

        mBroadLinkConnectionState =  broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"start to observe the connection state with ${it.name}: ${it.connectionState.name}")
                    it.observeConnectionStateChanges()
                }


        mState =   mBroadLinkConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"reading from the Characteristic")
                    it.readCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID))
                            .toObservable()}
                .flatMap { bytes ->
                    Observable.just(bytes[0].toInt()) }
                .flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<BroadLinkProfile.TvProfile.State>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(BroadLinkProfile.TvProfile.State.OFF))
                            1 ->  e.onNext(Resource.success(BroadLinkProfile.TvProfile.State.ON))
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
                      if(volume in BroadLinkProfile.TvProfile.MIN_VOLUME
                                           ..BroadLinkProfile.TvProfile.MAX_VOLUME)
                          e.onNext(Resource.success(volume))
                            else
                          e.onError(Throwable("inappropriate value"))
                        }
                    }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null) }

    }

    fun getTvConnectionState() = mBroadLinkConnectionState
    fun getTvPowerState() = mState
    fun getTvVolumeLevel() = mTvVolumeLevel

    fun setTvPowerState(state: BroadLinkProfile.TvProfile.State)
            = mBroadLinkConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.STATE_CHARACTERISTIC_UUID),
                            byteArrayOf(state.value.toByte())).toObservable()
                }
                .flatMap { bytes ->
                    Observable.create { e: ObservableEmitter<Int> ->
                        e.onNext(bytes[0].toInt())
                    }
                }
                .flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<BroadLinkProfile.TvProfile.State>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(BroadLinkProfile.TvProfile.State.OFF))
                            1 ->  e.onNext(Resource.success(BroadLinkProfile.TvProfile.State.ON))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }
                }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }


    fun setTvVolumeLevel(volume:Int)
          = mBroadLinkConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap { Observable.create { e: ObservableEmitter<RxBleConnection> ->
                    if(volume in BroadLinkProfile.TvProfile.MIN_VOLUME
                            .. BroadLinkProfile.TvProfile.MAX_VOLUME)
                        e.onNext(it)
                    else
                        e.onError(Throwable("inappropriate value",null))
                } }
                .flatMap { mTvVolumeLevel }
                .flatMap {
                    val currentVolume = it.data
                    if(volume > currentVolume!!)
                        mBroadLinkConnection.blockingLast().writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.VOLUME_UP_CHARACTERISTIC_UUID),
                            byteArrayOf(volume.toByte())).toObservable()
                    else
                        mBroadLinkConnection.blockingLast().writeCharacteristic(UUID.fromString(BroadLinkProfile.TvProfile.VOLUME_DOWN_CHARACTERISTIC_UUID),
                                byteArrayOf(volume.toByte())).toObservable()

                }
                .flatMap { bytes ->
                    Observable.create { e: ObservableEmitter<Int> ->
                        e.onNext(bytes[0].toInt())
                    }
                }
                .flatMap { volume ->
                    Observable.just(Resource.success(volume))
                }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }
}