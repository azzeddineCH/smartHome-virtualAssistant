package com.cerist.summer.virtualassistant.ViewModels

import ai.api.model.AIContext
import ai.api.model.Status
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.cerist.summer.virtualassistant.Repositories.DialogRepository
import com.cerist.summer.virtualassistant.Utils.ResponseParametersListing
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class DialogViewModel(private val mDialogRepository: DialogRepository):ViewModel(){

    companion object {
        val TAG = "DialogViewModel"
    }

    private val mDialogContexts: MutableLiveData<List<AIContext>> = MutableLiveData()
    private val mTextResponse: MutableLiveData<String> = MutableLiveData()
    private val mDialogRequestStatus:MutableLiveData<Status> = MutableLiveData()

    private val mDevicePowerStateSetAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()
    private val mDevicePowerStateCheckAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()
    private val mDeviceBrightnessSetAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()
    private val mDeviceBrightnessCheckAction:MutableLiveData<ResponseParametersListing> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    init {

        compositeDisposable.add(mDialogRepository.dialogTextResponse.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                mTextResponse::setValue,{
                             Log.d(TAG,"error dialogTextResponse: ${it.message}")
        }))

        compositeDisposable.add(mDialogRepository.dialogContexts.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                mDialogContexts::setValue,{
                             Log.d(TAG,"error dialogContexts: ${it.message}")
        }))

        compositeDisposable.add(mDialogRepository.dialogStatus.observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDialogRequestStatus::setValue,{
                    Log.d(TAG,"error dialogStatus: ${it.message}")
                }))

        compositeDisposable.add(mDialogRepository.devicePowerStateSetAction.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                mDevicePowerStateSetAction::setValue,{
                            Log.d(TAG,"error devicePowerStateSetAction: ${it.message}")
        }))

        compositeDisposable.add(mDialogRepository.devicePowerStateCheckAction.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                mDevicePowerStateCheckAction::setValue,{
                            Log.d(TAG,"error devicePowerStateCheckAction: ${it.message}")
        }))

        compositeDisposable.add(mDialogRepository.deviceBrightnessSetAction.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                mDevicePowerStateCheckAction::setValue,{
            Log.d(TAG,"error deviceBrightnessSetAction: ${it.message}")
        }))

        compositeDisposable.add(mDialogRepository.deviceBrightnessSetAction.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                mDevicePowerStateCheckAction::setValue,{
            Log.d(TAG,"error deviceBrightnessSetAction: ${it.message}")
        }))

        compositeDisposable.add(mDialogRepository.deviceBrightnessCheckAction.observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDeviceBrightnessCheckAction::setValue,{
            Log.d(TAG,"error deviceBrightnessCheckAction: ${it.message}")
        }))



    }

    fun getTextResponse():LiveData<String> = mTextResponse
    fun getDialogContext():LiveData<List<AIContext>> = mDialogContexts
    fun getDialogRequestStatus():LiveData<Status> = mDialogRequestStatus

    fun getDevicePowerStateSetAction():LiveData<ResponseParametersListing> = mDevicePowerStateSetAction
    fun getDevicePowerStateCheckAction():LiveData<ResponseParametersListing> = mDevicePowerStateCheckAction

    fun getDeviceBrightnessSetAction():LiveData<ResponseParametersListing> = mDeviceBrightnessSetAction
    fun getDeviceBrightnessCheckAction():LiveData<ResponseParametersListing> = mDeviceBrightnessCheckAction



    fun setDialogTextRequest(text:String){
        Log.d(TAG,"setDialogTextRequest $text")
        mDialogRepository.dialogTextRequest.onNext(text)

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}