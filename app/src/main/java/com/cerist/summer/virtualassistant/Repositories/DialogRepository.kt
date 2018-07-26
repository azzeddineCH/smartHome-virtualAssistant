package com.cerist.summer.virtualassistant.Repositories

import ai.api.AIDataService
import ai.api.AIServiceException
import ai.api.model.*
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Entities.ChatBotProfile
import com.cerist.summer.virtualassistant.Utils.Data.ResponseIntentListing
import com.cerist.summer.virtualassistant.Utils.Data.ResponseParametersListing
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.Executor

class DialogRepository( private val AIService:AIDataService,
                        private val networkExecutor: Executor) :IRepository{

    companion object {
        private val TAG = "DialogRepository"
    }



     var dialogTextRequest: PublishSubject<String> = PublishSubject.create()


     private var dialogResponse:Observable<AIResponse>  = dialogTextRequest.observeOn(Schedulers.from(networkExecutor))
              .flatMap {
                val aiRequest = AIRequest(it)
                Observable.create<AIResponse> {
                    try {
                        val response = AIService.request(aiRequest)
                        it.onNext(response)
                    }catch (e:AIServiceException){
                        it.onError(Throwable(com.cerist.summer.virtualassistant.Utils.Data.Status.OPERATION_ERROR))
                    }
                }
            }
              .share()


     val dialogStatus:Observable<Status> = dialogResponse.observeOn(Schedulers.from(networkExecutor))
             .flatMap {
                 Observable.just(it.status)
             }
             .share()


     val dialogContexts:Observable<List<AIContext>> = dialogResponse.observeOn(Schedulers.from(networkExecutor))
             .flatMap {
                Observable.just(it.result.contexts.map {
                    AIContext(it.name)
                })
            }
             .share()


     val dialogTextResponse:Observable<String> =  dialogResponse.observeOn(Schedulers.from(networkExecutor))
             .flatMap {
                 val text = it.result.fulfillment.speech
                 Observable.just(text)
             }
             .share()


    private val dialogIntentsDispatcher = dialogResponse.observeOn(Schedulers.from(networkExecutor))
            .flatMap {
                Log.d(TAG,"CHECKING ${it.result.action.substringBeforeLast(".")}")
              Observable.just(ResponseIntentListing(
                      action = it.result.action.substringBeforeLast("."),
                      parameters = it.result.parameters,
                      outputContexts = it.result.contexts.associateBy({ it.name }, { it })
              ))}
            .share()


    /**
     * Device Power state actions
     */

    val devicePowerStateSetAction:Observable<ResponseParametersListing> =  dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
             .filter{
                it.action == ChatBotProfile.DEVICE_SWITCH_SET_ACTION_KEY }
             .flatMap {
                val devices = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asJsonArray
                        ?: it.outputContexts[ChatBotProfile.DEVICE_SWITCH_CONTEXT]!!.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asJsonArray
                val state = it.parameters[ChatBotProfile.DEVICE_STATE_PARAMETER_KEY]?.asString
                Observable.just(Pair(devices,state)) }
             .filter {
                 it.first != null && it.second != null
             }
             .flatMap {
                    val state = it.second!!
                    val devices = it.first!!
                    val mapped = devices.map{
                     ResponseParametersListing(
                             device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(it.asString)),
                             powerState = ChatBotProfile.State.valueOf(ChatBotProfile.parameterValueMapper(state))
                     )
                 }.subList(0,devices.size())
                 Observable.fromIterable(mapped)}
             .share()


     val devicePowerStateCheckAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_SWITCH_CHECK_ACTION_KEY }
            .flatMap {
                val devices = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asJsonArray ?:
                         it.outputContexts[ChatBotProfile.DEVICE_SWITCH_CONTEXT]!!.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asJsonArray

                val mapped = devices!!.map{
                    ResponseParametersListing(
                            device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(it.asString))
                    )
                }.subList(0,devices.size())
                Observable.fromIterable(mapped)
            }
            .share()


    /**
     * Device Brightness Action
     */
    val deviceBrightnessSetAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter {
                it.action == ChatBotProfile.DEVICE_BRIGHTNESS_SET_ACTION_KEY }
            .flatMap {
                val luminosityLevel = it.parameters[ChatBotProfile.DEVICE_BRIGHTNESS_PARAMETER_KEY]?.asString
                val device = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asString ?: ChatBotProfile.Device.LAMP.name
                Observable.just(Pair(device,luminosityLevel))}
            .filter {
                it.second != null
            }
            .flatMap {
                val luminosityLevel = it.second!!
                val device = it.first
                Observable.just( ResponseParametersListing(
                        device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(device)),
                        luminosity = ChatBotProfile.Luminosity.valueOf(ChatBotProfile.parameterValueMapper(luminosityLevel))) )
            }
            .share()

    val deviceBrightnessCheckAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_BRIGHTNESS_CHECK_ACTION_KEY }
            .flatMap {
                val device = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asString ?: ChatBotProfile.Device.LAMP.name

                Observable.just( ResponseParametersListing(
                        device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(device))))
            }
            .share()



    /**
     * Device Mode Action
     */
    val deviceModeSetAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_MODE_SET_ACTION_KEY }
            .flatMap {
                val device = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asString ?: ChatBotProfile.Device.AIR_CONDITIONER.name
                val mode = it.parameters[ChatBotProfile.DEVICE_MODE_PARAMETER_KEY]?.asString

                Observable.just(Pair(device,mode))}
            .filter {
                it.second != null }
            .flatMap {
               Observable.just(
                    ResponseParametersListing(
                            device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(it.first)),
                            airMode = ChatBotProfile.AirMode.valueOf(ChatBotProfile.parameterValueMapper(it.second!!))
                    )) }
            .share()

    val deviceModeCheckAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_MODE_CHECK_ACTION_KEY }
            .flatMap {
                val device = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asString ?: ChatBotProfile.Device.AIR_CONDITIONER.name
                Observable.just(
                        ResponseParametersListing(
                                device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(device))
                        ))
            }
            .share()


    /**
     * Device Volume Actions
     */
    val deviceVolumeSetAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_VOLUME_SET_ACTION_KEY }
            .flatMap {
                val device = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asString ?: ChatBotProfile.Device.TV.name
                val number = it.parameters[ChatBotProfile.DEVICE_VOLUME_PARAMETER_KEY]?.asInt

                Observable.just(Pair(device, number)) }
            .filter { it.second != null }
            .flatMap {
                Observable.just(   ResponseParametersListing(
                        device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(it.first)),
                        volume = it.second
                ))
            }
            .share()

    val deviceVolumeCheckAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_VOLUME_CHECK_ACTION_KEY }
            .flatMap {
                val device = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asString ?: ChatBotProfile.Device.TV.name
                Observable.just(ResponseParametersListing(
                        device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(device))
                ))
            }
            .share()


    /**
     * Device timer Actions
     */

    val deviceTimerSetAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_TIMER_SET_ACTION_KEY }
            .flatMap {
                val device = ChatBotProfile.Device.TV.name
                val number = it.parameters[ChatBotProfile.DEVICE_TIMER_PARAMETER_KEY]?.asInt

                Observable.just(Pair(device, number)) }
            .filter { it.second != null }
            .flatMap {
                Log.d(TAG,"here we are")
                Observable.just(   ResponseParametersListing(
                        device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(it.first)),
                        timer = it.second
                ))
            }
            .share()

    val deviceTimerDisableAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_TIMER_DISABLE_ACTION_KEY }
            .flatMap {
                val device = ChatBotProfile.Device.TV.name
                Observable.just(   ResponseParametersListing(
                        device = ChatBotProfile.Device.valueOf(ChatBotProfile.parameterValueMapper(device)),
                        timer = BroadLinkProfile.TvProfile.TV_TIMER_SET[0]
                ))
            }
            .share()
}