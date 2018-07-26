package com.cerist.summer.virtualassistant.ViewModels

import ai.api.model.AIContext
import com.cerist.summer.virtualassistant.Utils.Data.Status
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Repositories.DialogRepository
import com.cerist.summer.virtualassistant.Utils.Data.ResponseParametersListing
import io.reactivex.disposables.CompositeDisposable

class DialogViewModel(private val mDialogRepository: DialogRepository):ViewModel(){

    companion object {
        val TAG = "DialogViewModel"
    }

    private val mDialogContexts: MutableLiveData<List<AIContext>> = MutableLiveData()
    private val mTextResponse: MutableLiveData<String> = MutableLiveData()
    private val mDialogErrorStatus:MutableLiveData<String> = MutableLiveData()


    private val mDevicePowerStateSetAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()
    private val mDevicePowerStateCheckAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()

    private val mDeviceBrightnessSetAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()
    private val mDeviceBrightnessCheckAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()

    private val mDeviceModeSetAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()
    private val mDeviceModeCheckAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()

    private val mDeviceVolumeSetAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()
    private val mDeviceVolumeCheckAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()

    private val mDeviceTimerSetAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()




    private val compositeDisposable = CompositeDisposable()

    init {

        compositeDisposable.add(mDialogRepository.dialogTextResponse.subscribe(
                mTextResponse::postValue){
           mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })

        compositeDisposable.add(mDialogRepository.dialogContexts.subscribe(
                mDialogContexts::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })

        compositeDisposable.add(mDialogRepository.dialogStatus.subscribe(
                {},{
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        }))

        compositeDisposable.add(mDialogRepository.devicePowerStateSetAction.subscribe(
                mDevicePowerStateSetAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })

        compositeDisposable.add(mDialogRepository.devicePowerStateCheckAction.subscribe(
                mDevicePowerStateCheckAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })

        compositeDisposable.add(mDialogRepository.deviceBrightnessSetAction.subscribe(
            mDeviceBrightnessSetAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })


        compositeDisposable.add(mDialogRepository.deviceBrightnessCheckAction.subscribe(
                mDeviceBrightnessCheckAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })


        compositeDisposable.add(mDialogRepository.deviceModeSetAction.subscribe(
                mDeviceModeSetAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })


        compositeDisposable.add(mDialogRepository.deviceModeCheckAction.subscribe(
                mDeviceModeCheckAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })

        compositeDisposable.add(mDialogRepository.deviceVolumeSetAction.subscribe(
                mDeviceVolumeSetAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })


        compositeDisposable.add(mDialogRepository.deviceVolumeCheckAction.subscribe(
                mDeviceVolumeCheckAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })

        compositeDisposable.add(mDialogRepository.deviceTimerSetAction.subscribe(
                mDeviceTimerSetAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })

        compositeDisposable.add(mDialogRepository.deviceTimerDisableAction.subscribe(
                mDeviceTimerSetAction::postValue){
            mDialogErrorStatus.postValue(Status.OPERATION_ERROR)
        })


    }

    fun getTextResponse():LiveData<String> = mTextResponse
    fun getDialogContext():LiveData<List<AIContext>> = mDialogContexts
    fun getDialogErrorStatus():LiveData<String> = mDialogErrorStatus

    fun getDevicePowerStateSetAction():LiveData<ResponseParametersListing> = mDevicePowerStateSetAction
    fun getDevicePowerStateCheckAction():LiveData<ResponseParametersListing> = mDevicePowerStateCheckAction

    fun getDeviceBrightnessSetAction():LiveData<ResponseParametersListing> = mDeviceBrightnessSetAction
    fun getDeviceBrightnessCheckAction():LiveData<ResponseParametersListing> = mDeviceBrightnessCheckAction

    fun getDeviceModeSetAction():LiveData<ResponseParametersListing> = mDeviceModeSetAction
    fun getDeviceModeCheckAction():LiveData<ResponseParametersListing> = mDeviceModeCheckAction

    fun getDeviceVolumeSetAction():LiveData<ResponseParametersListing> = mDeviceVolumeSetAction
    fun getDeviceVolumeCheckAction():LiveData<ResponseParametersListing> = mDeviceVolumeCheckAction

    fun getDeviceTimerSetAction():LiveData<ResponseParametersListing> = mDeviceTimerSetAction



    fun setDialogTextRequest(text:String){
        Log.d(TAG,"setDialogTextRequest $text")
        mDialogRepository.dialogTextRequest.onNext(text)

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}