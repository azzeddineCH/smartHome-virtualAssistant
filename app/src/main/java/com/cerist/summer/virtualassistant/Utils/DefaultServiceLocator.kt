package com.cerist.summer.virtualassistant.Utils

import android.support.v4.app.FragmentActivity
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Repositories.AirConditionerRepository
import com.cerist.summer.virtualassistant.Repositories.IRepository
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.cerist.summer.virtualassistant.Repositories.TvRepository
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class DefaultServiceLocator (private val activity: FragmentActivity): ServiceLocator {

    companion object {
      private  val TAG = "DefaultServiceLocator"
    }


    private var blueToothClient = RxBleClient.create(activity)
    private var rxPermissions = RxPermissions(activity)

    private val BLUETOOTH_IO = Executors.newFixedThreadPool(3)
    private val NETWORK_IO = Executors.newFixedThreadPool(2)

    private val bluetoothClientState:Observable<RxBleClient.State>
    private var lampBleDevice: Observable<RxBleDevice>
    private var broadLinkBleDevice: Observable<RxBleDevice>

    init {

    val bluetoothScan =   blueToothClient.scanBleDevices(
            ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
                    .build(),
            ScanFilter.Builder().build())
            .share()


        bluetoothClientState =  blueToothClient.observeStateChanges()
                                               .observeOn(Schedulers.from(getBlueToothExecutor()))
                                               .subscribeOn(AndroidSchedulers.mainThread())
                                               .startWith(Observable.just(blueToothClient.state))
                                               .share()




        broadLinkBleDevice = bluetoothScan.filter { it.bleDevice.macAddress ==  BroadLinkProfile.DEVICE_MAC_ADDRESS}
                                          .flatMap { result ->
                                                           Log.d(TAG, "the device named ${result.bleDevice.name} is found")
                                                  Observable.just(result.bleDevice) }
                                          .retry()
                                          .share()


        lampBleDevice =   bluetoothScan.filter { it.bleDevice.macAddress == LampProfile.DEVICE_MAC_ADDRESS }
                                       .flatMap { result ->
                                                      Log.d(TAG, "the device named ${result.bleDevice.name} is found")
                                              Observable.just(result.bleDevice) }
                                       .retry()
                                       .share()





    }



    override fun getBlueToothExecutor() = BLUETOOTH_IO

    override fun getNetworkExecutor() = NETWORK_IO

    override fun getBleDevice(key: BleDevices): Observable<RxBleDevice> {

        return when (key) {
            BleDevices.LAMP -> lampBleDevice
            BleDevices.BROAD_LINK -> broadLinkBleDevice
        }
    }

    override fun getRepository(key: Repositories): IRepository {
        return when (key) {
            Repositories.LAMP_REPOSITORY -> LampRepository(
                    lampBleDevice = getBleDevice(BleDevices.LAMP),
                    bluetoothExecutor = getBlueToothExecutor()
            )

            Repositories.TV_REPOSITORY -> TvRepository(
                    broadLink = getBleDevice(BleDevices.BROAD_LINK),
                    bluetoothExecutor = getBlueToothExecutor()

            )
            Repositories.AIR_CONDITIONER_REPOSITORY -> AirConditionerRepository(
                    broadLink =  getBleDevice(BleDevices.BROAD_LINK),
                    bluetoothExecutor = getBlueToothExecutor()
            )
        }
    }
}