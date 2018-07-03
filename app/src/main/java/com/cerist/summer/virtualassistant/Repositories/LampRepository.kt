package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class LampRepository(val lampBleDevice: Observable<RxBleDevice>,
                     val bluetoothExecutor: Executor) : IRepository {
    companion object {
        val TAG = "LampRepository"
    }

    private var  lampBleConnection: Observable<RxBleConnection> ?=null
    private var lampConnectionState:Observable<RxBleConnection.RxBleConnectionState> ?=null
    private val compositeDisposables = CompositeDisposable()

    init {

       val gattConnectionDisposable =  lampBleDevice.flatMap {
            Log.d(TAG,"connecting to the GATT server named ${it.name}")
            lampBleConnection = it.establishConnection(false)
            lampBleConnection
        }.subscribe({
                    Log.d(TAG,"success connecting to the GATT server")
                },{
                    Log.d(TAG,"error connecting to the GATT server named ${it.message}")
                })


        lampConnectionState =  lampBleDevice.flatMap {
            Log.d(TAG,"start to observe the connection state with ${it.name}: ${it.connectionState.name}")
           it.observeConnectionStateChanges()
        }

        lampBleConnection?.flatMap { bleConnexion ->
            Log.d(TAG,"reading from the Characteristic")
            bleConnexion.readCharacteristic(LampProfile.STATE_CHARACTERISTIC_UUID)
                    .toObservable()}
                ?.flatMap { bytes ->
                    Log.d(TAG,"the value is ${bytes[0]}")
                    Observable.just(bytes[0].toInt())
                }?.flatMap { value ->
                    Observable.create { e: ObservableEmitter<LampProfile.LAMP_STATE> ->
                        when (value) {
                            0 -> e.onNext(LampProfile.LAMP_STATE.OFF)
                            1 -> e.onNext(LampProfile.LAMP_STATE.ON)
                            else -> e.onError(Throwable("unknown value"))
                        }
                    }
                }?.subscribe ({
                    Log.d(TAG,"success connecting to the GATT server")
                },{
                    Log.d(TAG,"error connecting to the GATT server named ${it.message}")
                })

        compositeDisposables.addAll(gattConnectionDisposable)
    }



    fun getLampConnectionState() = lampConnectionState



    fun getLampState() = lampBleConnection?.flatMap { bleConnexion ->
        Log.d(TAG,"reading from the Characteristic")
        bleConnexion.readCharacteristic(LampProfile.STATE_CHARACTERISTIC_UUID)
                .toObservable()}
            ?.flatMap { bytes ->
                    Log.d(TAG,"the value is ${bytes[0]}")
                    Observable.just(bytes[0].toInt())
                }?.flatMap { value ->
                    Observable.create { e: ObservableEmitter<LampProfile.LAMP_STATE> ->
                        when (value) {
                            0 -> e.onNext(LampProfile.LAMP_STATE.OFF)
                            1 -> e.onNext(LampProfile.LAMP_STATE.ON)
                            else -> e.onError(Throwable("unknown value"))
                        }
                    }
                }



    fun getLampLuminosity() = lampBleConnection?.flatMap { bleConnexion ->
        bleConnexion.readCharacteristic(LampProfile.LUMINOSITY_CHARACTERISTIC_UUID)
                .flatMap { bleConnexion.setupNotification(LampProfile.LUMINOSITY_CHARACTERISTIC_UUID).singleOrError() }.toObservable()
    }?.flatMap { o -> o }
            ?.flatMap { bytes ->
                Observable.just(bytes[0].toInt())
            }?.flatMap { value ->
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

    fun setLampState(state: LampProfile.LAMP_STATE): Observable<LampProfile.LAMP_STATE> {
        val i: Byte = when (state) {
            LampProfile.LAMP_STATE.OFF -> 0
            LampProfile.LAMP_STATE.ON -> 1
        }

        return lampBleConnection!!
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(LampProfile.STATE_CHARACTERISTIC_UUID, byteArrayOf(i)).toObservable()
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

    fun setLampLumonisitiy(level: LampProfile.LAMP_LUMINOSITY): Observable<LampProfile.LAMP_LUMINOSITY> {
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

    fun clearDisposables(){
        compositeDisposables.clear()
    }
}