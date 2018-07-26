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


class AirConditionerRepository(private val broadLinkRepository: BroadLinkRepository,
                               private val bluetoothExecutor: Executor) : IRepository {

    companion object {
        private val TAG = "AirConditionerRepo"
    }

     val broadLinkConnection: Observable<RxBleConnection>
     val broadLinkConnectionState:Observable<RxBleConnection.RxBleConnectionState>



    init {
        broadLinkConnection =  broadLinkRepository.broadLinkConnection

        broadLinkConnectionState = broadLinkRepository.broadLinkConnectionState

    }


    fun getAirConditionerPowerState(bleConnection: RxBleConnection)
           =   Observable.just(bleConnection)
                         .observeOn(Schedulers.from(bluetoothExecutor))
                         .flatMap {
                            Log.d(TAG,"Reading the air conditioner power state characteristic")
                            it.readCharacteristic(UUID.fromString(BroadLinkProfile.AirConditionerProfile.STATE_CHARACTERISTIC_UUID))
                                    .toObservable()}
                        .flatMap {
                            Observable.just(it.toString(Charset.defaultCharset()).toInt()) }
                        .flatMap {
                            when (it) {
                                0 -> Observable.just(BroadLinkProfile.AirConditionerProfile.State.OFF)
                                1 ->  Observable.just(BroadLinkProfile.AirConditionerProfile.State.ON)
                                else -> Observable.error(Throwable(Status.OPERATION_ERROR))
                            }}
                        .share()!!

    fun getAirConditionerMode(bleConnection: RxBleConnection)
       =  Observable.just(bleConnection)
            .observeOn(Schedulers.from(bluetoothExecutor))
            .flatMap {
                Log.d(TAG,"Reading the air conditioner mode characteristic")
                it.readCharacteristic(UUID.fromString(BroadLinkProfile.AirConditionerProfile.MODE_CHARACTERISTIC_UUID))
                        .toObservable()}
            .flatMap {
                Observable.just(it.toString(Charset.defaultCharset()).toInt()) }
            .flatMap {
                when (it) {
                    0 -> Observable.just(BroadLinkProfile.AirConditionerProfile.Mode.SLEEP)
                    1 -> Observable.just(BroadLinkProfile.AirConditionerProfile.Mode.ENERGY_SAVER)
                    2 -> Observable.just(BroadLinkProfile.AirConditionerProfile.Mode.FUN)
                    3 -> Observable.just(BroadLinkProfile.AirConditionerProfile.Mode.COOL)
                    else -> Observable.error(Throwable(Status.OPERATION_ERROR))
                }}
            .share()!!

    fun getAirConditionerTemp(bleConnection: RxBleConnection)
        = Observable.just(bleConnection)
            .observeOn(Schedulers.from(bluetoothExecutor))
            .flatMap {
                Log.d(TAG,"Reading the air conditioner temperature characteristic")
                it.readCharacteristic(UUID.fromString(BroadLinkProfile.AirConditionerProfile.TEMPERATURE_UP_CHARACTERISTIC_UUID))
                        .toObservable()}
            .flatMap {
                Observable.just(it.toString(Charset.defaultCharset()).toInt()) }
            .flatMap {
                if(it in BroadLinkProfile.AirConditionerProfile.MIN_TEMP
                        .. BroadLinkProfile.AirConditionerProfile.MAX_TEMP)
                    Observable.just(it)
                else
                    Observable.error(Throwable(Status.OPERATION_ERROR))
            }
            .share()!!

    fun setAirConditionerPowerState(bleConnection: RxBleConnection,state: BroadLinkProfile.AirConditionerProfile.State)
           =  Observable.just(bleConnection)
                        .observeOn(Schedulers.from(bluetoothExecutor))
                        .flatMap {
                            Log.d(TAG,"Writing the air conditioner power state characteristic")
                            it.writeCharacteristic(UUID.fromString( BroadLinkProfile.AirConditionerProfile.STATE_CHARACTERISTIC_UUID),
                                    byteArrayOf(state.value.toByte())).toObservable()
                        }
                        .flatMap {
                               Observable.just(it[0].toInt())
                        }
                        .flatMap {
                                when (it) {
                                    0 -> Observable.just(BroadLinkProfile.AirConditionerProfile.State.OFF)
                                    1 ->  Observable.just(BroadLinkProfile.AirConditionerProfile.State.ON)
                                    else -> Observable.error(Throwable(Status.OPERATION_ERROR))
                                }
                        }
                        .share()!!



    fun setAirConditionerMode(bleConnection: RxBleConnection,mode:BroadLinkProfile.AirConditionerProfile.Mode)
           =  Observable.just(bleConnection)
                        .observeOn(Schedulers.from(bluetoothExecutor))
                        .flatMap {
                            Log.d(TAG,"Writing the air conditioner mode characteristic")
                            it.writeCharacteristic(UUID.fromString( BroadLinkProfile.AirConditionerProfile.MODE_CHARACTERISTIC_UUID), byteArrayOf(mode.value.toByte())).toObservable()
                        }
                        .flatMap {
                            Observable.just(it[0].toInt())
                        }
                        .flatMap {
                                when (it) {
                                    0 ->  Observable.just(BroadLinkProfile.AirConditionerProfile.Mode.SLEEP)
                                    1 ->  Observable.just(BroadLinkProfile.AirConditionerProfile.Mode.ENERGY_SAVER)
                                    2 ->  Observable.just(BroadLinkProfile.AirConditionerProfile.Mode.FUN)
                                    3 ->  Observable.just(BroadLinkProfile.AirConditionerProfile.Mode.COOL)
                                    else -> Observable.error(Throwable(Status.OPERATION_ERROR))
                                }}
                        .share()!!

    fun setAirConditionerTemp(bleConnection: RxBleConnection,temperature:Int)
            =  Observable.just(bleConnection)
                         .observeOn(Schedulers.from(bluetoothExecutor))
                        .flatMap {
                            if(temperature in BroadLinkProfile.AirConditionerProfile.MIN_TEMP
                                    .. BroadLinkProfile.AirConditionerProfile.MAX_TEMP)
                                Observable.just(it)
                            else
                                Observable.error(Throwable(Status.OPERATION_ERROR))
                        }
                        .flatMap { getAirConditionerTemp(it) }
                        .flatMap {
                                    Log.d(TAG,"Writing  the air conditioner temperature characteristic")
                                    if(temperature > it)
                                        bleConnection.writeCharacteristic(UUID.fromString( BroadLinkProfile.AirConditionerProfile.TEMPERATURE_UP_CHARACTERISTIC_UUID),
                                                byteArrayOf(temperature.toByte())).toObservable()
                                    else
                                        bleConnection.writeCharacteristic(UUID.fromString( BroadLinkProfile.AirConditionerProfile.TEMPERATURE_DOWN_CHARACTERISTIC_UUID),
                                                byteArrayOf(temperature.toByte())).toObservable()
                                }
                        .flatMap{
                            Observable.just(it[0].toInt())
                        }
                        .share()!!

}