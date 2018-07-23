package com.cerist.summer.virtualassistant.Repositories

import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Utils.Data.Status
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executor

class LampRepository(private val lampBleDevice: Observable<RxBleDevice>,
                     private val bluetoothExecutor: Executor) : IRepository {

    companion object {
       private val TAG = "LampRepository"
    }

     val  lampConnectionState:Observable<RxBleConnection.RxBleConnectionState>
     val  lampBleConnection: Observable<RxBleConnection>

    init {

        lampBleConnection = lampBleDevice.observeOn(Schedulers.from(bluetoothExecutor))
                                          .flatMap {
                                              Log.d(TAG, "Connecting to the lamp GATT server")
                                                      it.establishConnection(true)}
                                          .retry()
                                          .share()



         lampConnectionState =  lampBleDevice.observeOn(Schedulers.from(bluetoothExecutor))
                                            .flatMap {
                                                        Log.d(TAG,"Observing the Lamp GATT server connection state")
                                                        it.observeConnectionStateChanges()}
                                            .share()

    }


    fun getLampLuminosityLevel(bleConnection: RxBleConnection)
        =   Observable.just(bleConnection)
            .observeOn(Schedulers.from(bluetoothExecutor))
            .flatMap {
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
                    else -> Observable.error(Throwable(Status.OPERATION_ERROR)) }}
            .share()!!


    fun getLampPowerState(bleConnection: RxBleConnection)
        =   Observable.just(bleConnection)
                        .observeOn(Schedulers.from(bluetoothExecutor))
                        .flatMap {
                                  it.readCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID))
                                          .toObservable()
                        }
                        .flatMap {
                                Observable.just(it[0].toInt()) }
                        .flatMap {
                when (it) {
                    0 -> Observable.just(LampProfile.State.OFF)
                    1 ->  Observable.just(LampProfile.State.ON)
                    else -> Observable.error(Throwable(Status.OPERATION_ERROR)) }}
                        .share()!!


    fun setLampPowerState(bleConnection: RxBleConnection,state: LampProfile.State)
            =  Observable.just(bleConnection)
                         .observeOn(Schedulers.from(bluetoothExecutor))
                         .flatMap {
                                 it.writeCharacteristic(UUID.fromString(LampProfile.STATE_CHARACTERISTIC_UUID),
                                                                byteArrayOf(state.value.toByte()))
                                                .toObservable() }
                         .flatMap {
                             Observable.just(it[0].toInt()) }
                         .flatMap {
                                when (it) {
                                    0 -> Observable.just(LampProfile.State.OFF)
                                    1 ->  Observable.just(LampProfile.State.ON)
                                    else -> Observable.error(Throwable(Status.OPERATION_ERROR)) }}
                         .share()!!




    fun setLampLuminosityLevel(bleConnection: RxBleConnection,level: LampProfile.Luminosity)
             =  Observable.just(bleConnection)
                          .observeOn(Schedulers.from(bluetoothExecutor))
                          .flatMap {
                                 it.writeCharacteristic(UUID.fromString(LampProfile.LUMINOSITY_CHARACTERISTIC_UUID), byteArrayOf(level.value.toByte()))
                                         .toObservable() }
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
                                    else -> Observable.error(Throwable(Status.OPERATION_ERROR)) }}
                           .share()!!


    }
