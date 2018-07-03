package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executor

class LampRepository(val lampBleDevice: Observable<RxBleDevice>,
                     val bluetoothExecutor: Executor) : IRepository {
    companion object {
        val TAG = "LampRepository"
    }

    private val  lampBleConnection: ConnectableObservable<RxBleConnection>
    private val  lampConnectionState:Observable<RxBleConnection.RxBleConnectionState>
    private val  lampLightningState:Observable<LampProfile.LAMP_STATE>
    private val  lampLuminosity:Observable<LampProfile.LAMP_LUMINOSITY>


    init {

        lampBleConnection =  lampBleDevice.observeOn(Schedulers.from(bluetoothExecutor))
                                          .subscribeOn(AndroidSchedulers.mainThread())
                                          .flatMap {
                                                  Log.d(TAG,"connecting to the GATT server named ${it.name}")
                                              it.establishConnection(false)
                                          }.publish()


        lampConnectionState =  lampBleDevice.observeOn(Schedulers.from(bluetoothExecutor))
                                            .subscribeOn(AndroidSchedulers.mainThread())
                                            .flatMap {
                                                        Log.d(TAG,"start to observe the connection state with ${it.name}: ${it.connectionState.name}")
                                                        it.observeConnectionStateChanges()
                                             }


        lampLightningState =   lampBleConnection.observeOn(Schedulers.from(bluetoothExecutor))
                                                .subscribeOn(AndroidSchedulers.mainThread())
                                                .flatMap {
                                                        Log.d(TAG,"reading from the Characteristic")
                                                        it.readCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID))
                                                               .toObservable()}
                                                .flatMap { bytes ->
                                                    Observable.just(bytes[0].toInt()) }
                                                .flatMap { value ->
                                                    Observable.create { e: ObservableEmitter<LampProfile.LAMP_STATE> ->
                                                        when (value) {
                                                            0 -> e.onNext(LampProfile.LAMP_STATE.OFF)
                                                            1 -> e.onNext(LampProfile.LAMP_STATE.ON)
                                                            else -> e.onError(Throwable("unknown value"))
                                                        }
                                                    }}


        lampLuminosity = lampBleConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap { 
                    it.readCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID))
                        .toObservable()}
                .flatMap { bytes ->
                    Observable.just(bytes[0].toInt()) }
                .flatMap { value ->
                    Observable.create { e: ObservableEmitter<LampProfile.LAMP_LUMINOSITY> ->
                        when (value) {
                            0 -> e.onNext(LampProfile.LAMP_LUMINOSITY.NON)
                            1 -> e.onNext(LampProfile.LAMP_LUMINOSITY.LOW)
                            2 -> e.onNext(LampProfile.LAMP_LUMINOSITY.MEDIUM)
                            3 -> e.onNext(LampProfile.LAMP_LUMINOSITY.HIGH)
                            4 -> e.onNext(LampProfile.LAMP_LUMINOSITY.MAX)
                            else -> e.onError(Throwable("unknown value"))
                        }
                    }}



         lampConnectionState.subscribe {
            Log.d(TAG,"connecting to the GATT server named ${it.name}")
            if(it == RxBleConnection.RxBleConnectionState.CONNECTED) lampBleConnection.connect()
        }

       //  lampBleConnection.connect()

    }



    fun getLampConnectionState() = lampConnectionState

    fun getLampLightningState() = lampLightningState

    fun getLampLuminosityLevel() = lampLuminosity

    fun setLampLightningState(state: LampProfile.LAMP_STATE): Observable<LampProfile.LAMP_STATE> {
        val i: Byte = when (state) {
            LampProfile.LAMP_STATE.OFF -> 0
            LampProfile.LAMP_STATE.ON -> 1
        }

        return lampBleConnection!!
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID), byteArrayOf(i)).toObservable()
                }.flatMap { bytes ->
                    Observable.create { e: ObservableEmitter<Int> ->
                        e.onNext(bytes[0].toInt())
                    }
                }.flatMap { value ->
                    Observable.create { e: ObservableEmitter<LampProfile.LAMP_STATE> ->
                        when (value) {
                            0 -> e.onNext(LampProfile.LAMP_STATE.OFF)
                            1 -> e.onNext(LampProfile.LAMP_STATE.ON)
                            else -> e.onError(Throwable("unknown value"))
                        }
                    }
                }

    }

    fun getLampLuminosityLevel(level: LampProfile.LAMP_LUMINOSITY): Observable<LampProfile.LAMP_LUMINOSITY> {
        val i: Byte = when (level) {
            LampProfile.LAMP_LUMINOSITY.NON -> 0
            LampProfile.LAMP_LUMINOSITY.LOW -> 1
            LampProfile.LAMP_LUMINOSITY.MEDIUM -> 2
            LampProfile.LAMP_LUMINOSITY.HIGH -> 3
            LampProfile.LAMP_LUMINOSITY.MAX -> 4
        }

        return lampBleConnection!!
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(LampProfile.LUMINOSITY_CHARACTERISTIC_UUID, byteArrayOf(i)).toObservable()
                }.flatMap { bytes ->
                    Observable.create { e: ObservableEmitter<Int> -> e.onNext(bytes[0].toInt()) }
                }.flatMap { value ->
                    Observable.create { e: ObservableEmitter<LampProfile.LAMP_LUMINOSITY> ->
                        when (value) {
                            0 -> e.onNext(LampProfile.LAMP_LUMINOSITY.NON)
                            1 -> e.onNext(LampProfile.LAMP_LUMINOSITY.LOW)
                            2 -> e.onNext(LampProfile.LAMP_LUMINOSITY.MEDIUM)
                            3 -> e.onNext(LampProfile.LAMP_LUMINOSITY.HIGH)
                            4 -> e.onNext(LampProfile.LAMP_LUMINOSITY.MAX)
                            else -> e.onError(Throwable("unknown value"))
                        }
                    }


                }
    }
    
}