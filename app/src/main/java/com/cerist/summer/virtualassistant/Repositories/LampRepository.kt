package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
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

class LampRepository(val lampBleDevice: Observable<RxBleDevice>,
                     val bluetoothExecutor: Executor) : IRepository {
    companion object {
        val TAG = "LampRepository"
    }

    private val  mLampBleConnection: ConnectableObservable<RxBleConnection>
    private val  mLampConnectionState:Observable<RxBleConnection.RxBleConnectionState>

    private val  mLampLightningState:Observable<Resource<LampProfile.LAMP_STATE>>
    private val  mLampLuminosityLevel:Observable<Resource<LampProfile.LAMP_LUMINOSITY>>


    init {

        mLampBleConnection =  lampBleDevice.observeOn(Schedulers.from(bluetoothExecutor))
                                          .subscribeOn(AndroidSchedulers.mainThread())
                                          .flatMap {
                                                  Log.d(TAG,"connecting to the GATT server named ${it.name}")
                                              it.establishConnection(false)
                                          }.publish()


        mLampConnectionState =  lampBleDevice.observeOn(Schedulers.from(bluetoothExecutor))
                                            .subscribeOn(AndroidSchedulers.mainThread())
                                            .flatMap {
                                                        Log.d(TAG,"start to observe the connection state with ${it.name}: ${it.connectionState.name}")
                                                        it.observeConnectionStateChanges()
                                             }



        mLampLightningState =   mLampBleConnection.observeOn(Schedulers.from(bluetoothExecutor))
                                                .subscribeOn(AndroidSchedulers.mainThread())
                                                .flatMap {
                                                        Log.d(TAG,"reading from the Characteristic")
                                                        it.readCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID))
                                                               .toObservable()}
                                                .flatMap { bytes ->
                                                    Observable.just(bytes[0].toInt()) }
                                                .flatMap { value ->
                                                    Observable.create { e: ObservableEmitter<Resource<LampProfile.LAMP_STATE>> ->
                                                        when (value) {
                                                            0 -> e.onNext(Resource.success(LampProfile.LAMP_STATE.OFF))
                                                            1 ->  e.onNext(Resource.success(LampProfile.LAMP_STATE.ON))
                                                            else -> e.onNext(Resource.error("unknown value",null))
                                                        }
                                                    }}
                                                .onErrorReturn { t:Throwable ->
                                                            Resource.error("${t.message}",null)
                                                }


        mLampLuminosityLevel = mLampBleConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap { 
                    it.readCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID))
                        .toObservable()}
                .flatMap { bytes ->
                    Observable.just(bytes[0].toInt()) }
                .flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<LampProfile.LAMP_LUMINOSITY>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.NON))
                            1 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.LOW))
                            2 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.MEDIUM))
                            3 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.HIGH))
                            4 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.MAX))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }}
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }



         mLampBleConnection.connect()


    }



    fun getLampConnectionState() = mLampConnectionState

    fun getLampLightningState() = mLampLightningState

    fun getLampLuminosityLevel() = mLampLuminosityLevel

    fun setLampLightningState(state: LampProfile.LAMP_STATE): Observable<Resource<LampProfile.LAMP_STATE>> {
        val i: Byte = when (state) {
            LampProfile.LAMP_STATE.OFF -> 0
            LampProfile.LAMP_STATE.ON -> 1
        }

        return mLampBleConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID), byteArrayOf(i)).toObservable()
                }.flatMap { bytes ->
                    Observable.create { e: ObservableEmitter<Int> ->
                        e.onNext(bytes[0].toInt())
                    }
                }.flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<LampProfile.LAMP_STATE>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(LampProfile.LAMP_STATE.OFF))
                            1 ->  e.onNext(Resource.success(LampProfile.LAMP_STATE.ON))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }
                }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }

    }

    fun getLampLuminosityLevel(level: LampProfile.LAMP_LUMINOSITY): Observable<Resource<LampProfile.LAMP_LUMINOSITY>> {
        val i: Byte = when (level) {
            LampProfile.LAMP_LUMINOSITY.NON -> 0
            LampProfile.LAMP_LUMINOSITY.LOW -> 1
            LampProfile.LAMP_LUMINOSITY.MEDIUM -> 2
            LampProfile.LAMP_LUMINOSITY.HIGH -> 3
            LampProfile.LAMP_LUMINOSITY.MAX -> 4
        }

        return mLampBleConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(LampProfile.LUMINOSITY_CHARACTERISTIC_UUID, byteArrayOf(i)).toObservable()
                }.flatMap { bytes ->
                    Observable.create { e: ObservableEmitter<Int> -> e.onNext(bytes[0].toInt()) }
                }.flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<LampProfile.LAMP_LUMINOSITY>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.NON))
                            1 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.LOW))
                            2 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.MEDIUM))
                            3 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.HIGH))
                            4 -> e.onNext(Resource.success(LampProfile.LAMP_LUMINOSITY.MAX))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }
                }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }

    }
    
}