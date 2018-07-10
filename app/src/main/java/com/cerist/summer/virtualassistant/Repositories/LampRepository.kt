package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executor

class LampRepository(private val lampBleDevice: Observable<RxBleDevice>,
                     private val bluetoothExecutor: Executor) : IRepository {

    companion object {
       private val TAG = "LampRepository"
    }

     val  lampBleConnection: Observable<RxBleConnection>
     val  lampConnectionState:Observable<RxBleConnection.RxBleConnectionState>

     val  lampPowerState:Observable<LampProfile.State>
     val  lampLuminosityLevel:Observable<LampProfile.Luminosity>


    init {

        lampBleConnection =  lampBleDevice.observeOn(Schedulers.from(bluetoothExecutor))
                                          .subscribeOn(AndroidSchedulers.mainThread())
                                          .flatMap {
                                                  Log.d(TAG,"Connecting to the lamp GATT server")
                                              it.establishConnection(true)
                                          }
                                          .retry()
                                          .share()



        lampConnectionState =  lampBleDevice.observeOn(Schedulers.from(bluetoothExecutor))
                                            .subscribeOn(AndroidSchedulers.mainThread())
                                            .flatMap {
                                                        Log.d(TAG,"Observing the Lamp GATT server connection state")
                                                        it.observeConnectionStateChanges()}
                                            .share()




        lampPowerState =   lampBleConnection.observeOn(Schedulers.from(bluetoothExecutor))
                                                .subscribeOn(AndroidSchedulers.mainThread())
                                                .flatMap {
                                                        Log.d(TAG,"Reading the lamp power state characteristic")
                                                        it.readCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID))
                                                               .toObservable()}
                                                .flatMap {
                                                    Observable.just(it[0].toInt()) }
                                                .flatMap {
                                                        when (it) {
                                                            0 -> Observable.just(LampProfile.State.OFF)
                                                            1 ->  Observable.just(LampProfile.State.ON)
                                                            else ->  Observable.error(Throwable("unknown value ${it}"))
                                                        } }
                                                .share()




        lampLuminosityLevel = lampBleConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(TAG,"Reading the lamp luminosity characteristic")
                    it.readCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID))
                        .toObservable()}
                .flatMap {
                    Observable.just(it[0].toInt()) }
                .flatMap {
                        when (it) {
                            0 -> Observable.just(LampProfile.Luminosity.NON)
                            1 -> Observable.just(LampProfile.Luminosity.LOW)
                            2 -> Observable.just(LampProfile.Luminosity.MEDIUM)
                            3 -> Observable.just(LampProfile.Luminosity.HIGH)
                            4 -> Observable.just(LampProfile.Luminosity.MAX)
                            else -> Observable.error(Throwable("unknown value ${it}"))
                        }}
                .share()



    }




    fun setLampPowerState(state: LampProfile.State)
            =lampBleConnection
                        .observeOn(Schedulers.from(bluetoothExecutor))
                        .flatMap {
                            Log.d(TAG,"Writing the lamp power state characteristic")
                            it.writeCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID), byteArrayOf(state.value.toByte())).toObservable()
                        }
                        .flatMap {
                                Observable.just(it[0].toInt())

                        }
                        .flatMap {
                                when (it) {
                                    0 -> Observable.just(LampProfile.State.OFF)
                                    1 ->  Observable.just(LampProfile.State.ON)
                                    else -> Observable.error(Throwable("unknown value"))
                                }
                        }
                        .share()!!


    fun setLampLuminosityLevel(level: LampProfile.Luminosity)
          =lampBleConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    Log.d(TAG,"Writing the lamp luminosity characteristic")
                    it.writeCharacteristic(UUID.fromString(LampProfile.LUMINOSITY_CHARACTERISTIC_UUID), byteArrayOf(level.value.toByte())).toObservable()
                }
                .flatMap {
                    Observable.just(it[0].toInt())
                }
                .flatMap {
                        when (it) {
                            0 -> Observable.just(LampProfile.Luminosity.NON)
                            1 -> Observable.just(LampProfile.Luminosity.LOW)
                            2 -> Observable.just(LampProfile.Luminosity.MEDIUM)
                            3 -> Observable.just(LampProfile.Luminosity.HIGH)
                            4 -> Observable.just(LampProfile.Luminosity.MAX)
                            else -> Observable.error(Throwable("unknown value"))
                        }
                }
                .share()!!

    }
