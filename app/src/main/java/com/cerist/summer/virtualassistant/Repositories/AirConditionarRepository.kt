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


class AirConditionarRepository(val broadLink: Observable<RxBleDevice>,
                               val bluetoothExecutor: Executor) : IRepository {

    companion object {
        val TAG = "AirConditionarRepository"
    }

    private val  mBroadLinkConnection: ConnectableObservable<RxBleConnection>
    private val  mBroadLinkConnectionState:Observable<RxBleConnection.RxBleConnectionState>

    private val mAirConditionerState:Observable<Resource<BroadLinkProfile.AirConditionerProfile.State>>
    private val mMode:Observable<Resource<BroadLinkProfile.AirConditionerProfile.Mode>>
    private val mAirConditionerTemp:Observable<Resource<Int>>

    init {
        mBroadLinkConnection =  broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"connecting to the GATT server named ${it.name}")
                    it.establishConnection(false) }
                .publish()

        mBroadLinkConnectionState =  broadLink.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"start to observe the connection state with ${it.name}: ${it.connectionState.name}")
                    it.observeConnectionStateChanges()
                }


        mAirConditionerState =   mBroadLinkConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.d(LampRepository.TAG,"reading from the Characteristic")
                    it.readCharacteristic(UUID.fromString(BroadLinkProfile.AirConditionerProfile.STATE_CHARACTERISTIC_UUID))
                            .toObservable()}
                .flatMap { bytes ->
                    Observable.just(bytes[0].toInt()) }
                .flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<BroadLinkProfile.AirConditionerProfile.State>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.State.OFF))
                            1 ->  e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.State.ON))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }}
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }

        mMode = mBroadLinkConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    it.readCharacteristic(UUID.fromString(BroadLinkProfile.AirConditionerProfile.MODE_CHARACTERISTIC_UUID))
                            .toObservable()}
                .flatMap { bytes ->
                    Observable.just(bytes[0].toInt()) }
                .flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<BroadLinkProfile.AirConditionerProfile.Mode>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.Mode.SLEEP))
                            1 -> e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.Mode.ENERGY_SAVER))
                            2 -> e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.Mode.FUN))
                            3 -> e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.Mode.COOL))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }}
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }


        mAirConditionerTemp = mBroadLinkConnection.observeOn(Schedulers.from(bluetoothExecutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    it.readCharacteristic(UUID.fromString(BroadLinkProfile.AirConditionerProfile.TEMPERATURE_UP_CHARACTERISTIC_UUID))
                            .toObservable()}
                .flatMap { bytes ->
                    Observable.just(bytes[0].toInt()) }
                .flatMap { temp ->
                    Observable.create { e: ObservableEmitter<Resource<Int>> ->
                        if(temp in BroadLinkProfile.AirConditionerProfile.MIN_TEMP
                                    .. BroadLinkProfile.AirConditionerProfile.MAX_TEMP)
                            e.onNext(Resource.success(temp))
                        else
                            e.onNext(Resource.error("inappropriate value",null))
                    }}
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }

    }

    fun getAirConditionConnectionState() = mBroadLinkConnectionState
    fun getAirConditionerPowerState() = mAirConditionerState
    fun getAirConditionerMode() = mMode
    fun getAirConditionerTemp() = mAirConditionerTemp


    fun setAirConditionerPowerState(state: BroadLinkProfile.AirConditionerProfile.State)
           =  mBroadLinkConnection
                .observeOn(Schedulers.from(bluetoothExecutor))
                .flatMap {
                    it.writeCharacteristic(UUID.fromString( BroadLinkProfile.AirConditionerProfile.STATE_CHARACTERISTIC_UUID), byteArrayOf(state.value.toByte())).toObservable()
                }
                .flatMap { bytes ->
                       Observable.just(bytes[0].toInt())

                }
                .flatMap { value ->
                    Observable.create { e: ObservableEmitter<Resource<BroadLinkProfile.AirConditionerProfile.State>> ->
                        when (value) {
                            0 -> e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.State.OFF))
                            1 ->  e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.State.ON))
                            else -> e.onNext(Resource.error("unknown value",null))
                        }
                    }
                }
                .onErrorReturn { t:Throwable ->
                    Resource.error("${t.message}",null)
                }



    fun setAirConditionerMode(mode:BroadLinkProfile.AirConditionerProfile.Mode)
           =  mBroadLinkConnection
            .observeOn(Schedulers.from(bluetoothExecutor))
            .flatMap {
                it.writeCharacteristic(UUID.fromString( BroadLinkProfile.AirConditionerProfile.MODE_CHARACTERISTIC_UUID), byteArrayOf(mode.value.toByte())).toObservable()
            }
            .flatMap { bytes ->
                Observable.just(bytes[0].toInt())
            }
            .flatMap { value ->
                Observable.create { e: ObservableEmitter<Resource<BroadLinkProfile.AirConditionerProfile.Mode>> ->
                    when (value) {
                        0 ->  e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.Mode.SLEEP))
                        1 ->  e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.Mode.ENERGY_SAVER))
                        2 ->  e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.Mode.FUN))
                        3 ->  e.onNext(Resource.success(BroadLinkProfile.AirConditionerProfile.Mode.COOL))
                        else -> e.onNext(Resource.error("unknown value",null))
                    }
                }
            }
            .onErrorReturn { t:Throwable ->
                Resource.error("${t.message}",null)
            }

    fun setAirConditionerTemp(temperature:Int)
            =  mBroadLinkConnection
            .observeOn(Schedulers.from(bluetoothExecutor))
            .flatMap {
                Observable.create { e: ObservableEmitter<RxBleConnection> ->
                if(temperature in BroadLinkProfile.AirConditionerProfile.MIN_TEMP
                        .. BroadLinkProfile.AirConditionerProfile.MAX_TEMP)
                    e.onNext(it)
                else
                    e.onError(Throwable("inappropriate value",null))
            }}
            .flatMap { mAirConditionerTemp }
            .flatMap {
                val currentTemperature = it.data
                if(temperature > currentTemperature!!)
                    mBroadLinkConnection.blockingLast().writeCharacteristic(UUID.fromString( BroadLinkProfile.AirConditionerProfile.TEMPERATURE_UP_CHARACTERISTIC_UUID),
                            byteArrayOf(temperature.toByte())).toObservable()
                else
                    mBroadLinkConnection.blockingLast().writeCharacteristic(UUID.fromString( BroadLinkProfile.AirConditionerProfile.TEMPERATURE_DOWN_CHARACTERISTIC_UUID),
                            byteArrayOf(temperature.toByte())).toObservable()
            }
            .flatMap{ bytes ->
                Observable.just(bytes[0].toInt())
            }
            .flatMap { value ->
                Observable.just(Resource.success(value))
            }
            .onErrorReturn { t:Throwable ->
                Resource.error("${t.message}",null)
            }



}