package com.cerist.summer.virtualassistant.Utils


import ai.api.android.AIConfiguration
import ai.api.AIDataService
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Repositories.*
import com.cerist.summer.virtualassistant.Utils.Functions.requestLocationPermissions
import com.jakewharton.rx.replayingShare
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DefaultServiceLocator (private val activity: FragmentActivity): ServiceLocator {

    companion object {
      private  val TAG = "DefaultServiceLocator"
    }


    private var blueToothClient = RxBleClient.create(activity)

    private val BLUETOOTH_IO = Executors.newFixedThreadPool(2)
    private val NETWORK_IO = Executors.newFixedThreadPool(1)
    private val bluetoothScan:Observable<ScanResult>
    private val bluetoothClientState:Observable<RxBleClient.State>
    private val lampBleDevice: Observable<RxBleDevice>
    private val broadLinkBleDevice: Observable<RxBleDevice>
    private val broadLinkRepository:BroadLinkRepository
    private val mConfig = AIConfiguration(
            "e87e26ceb2ae4519ace2f3c71abd076e",
                             ai.api.AIConfiguration.SupportedLanguages.English,
                             AIConfiguration.RecognitionEngine.System
    )
    private val AIDataService = AIDataService(mConfig)

    init {




        bluetoothClientState =  blueToothClient.observeStateChanges()
                                               .observeOn(Schedulers.from(getBlueToothExecutor()))
                                               .subscribeOn(AndroidSchedulers.mainThread())
                                               .startWith(Observable.just(blueToothClient.state))
                                               .share()


         bluetoothScan =  bluetoothClientState.observeOn(Schedulers.from(getBlueToothExecutor()))
                 .filter{it == RxBleClient.State.READY}
                 .delay(1000,TimeUnit.MILLISECONDS)
                 .flatMap {
                    blueToothClient.scanBleDevices(
                            ScanSettings.Builder()
                                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                    .build(),
                            ScanFilter.Builder().build())}
                 .share()






        broadLinkBleDevice = bluetoothScan.observeOn(Schedulers.from(getBlueToothExecutor()))
                                          .filter { it.bleDevice.macAddress ==  BroadLinkProfile.DEVICE_MAC_ADDRESS}
                                          .flatMap { result ->
                                                           Log.d(TAG, "the device named ${result.bleDevice.name} is found")
                                                  Observable.just(result.bleDevice) }
                                          .share()
                                          .retry()





        lampBleDevice =   bluetoothScan.observeOn(Schedulers.from(getBlueToothExecutor()))
                                        .filter { it.bleDevice.macAddress == LampProfile.DEVICE_MAC_ADDRESS }
                                        .flatMap { result ->
                                                      Log.d(TAG, "the device named ${result.bleDevice.name} is found")
                                           Observable.just(result.bleDevice)}
                                        .share()
                                        .retry()




        broadLinkRepository = BroadLinkRepository(broadLinkBleDevice,getBlueToothExecutor())



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
                    broadLinkRepository = broadLinkRepository,
                    bluetoothExecutor = getBlueToothExecutor()

            )
            Repositories.AIR_CONDITIONER_REPOSITORY -> AirConditionerRepository(
                    broadLinkRepository = broadLinkRepository,
                    bluetoothExecutor = getBlueToothExecutor()
            )
            Repositories.DIALOG_REPOSITORY -> DialogRepository(
                    AIService = AIDataService,
                    networkExecutor = getNetworkExecutor()

            )
        }
    }
}