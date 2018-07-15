package com.cerist.summer.virtualassistant.Repositories

import ai.api.AIDataService
import ai.api.AIServiceException
import ai.api.model.*
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.ChatBotProfile
import com.cerist.summer.virtualassistant.Utils.ResponseIntentListing
import com.cerist.summer.virtualassistant.Utils.ResponseParametersListing
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

     var dialogResponse:Observable<AIResponse>  = dialogTextRequest.observeOn(Schedulers.from(networkExecutor))
             .flatMap {
                val aiRequest = AIRequest(it)
                Observable.create<AIResponse> {
                    try {
                        val response = AIService.request(aiRequest)
                        it.onNext(response)
                    }catch (e:AIServiceException){
                        it.onError(Throwable("error: ${e.message}"))
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
                                                                Log.d(TAG,"the response is ${text}")
                                                                Observable.just(text)
                                                          }
             .share()


    private val dialogIntentsDispatcher = dialogResponse.observeOn(Schedulers.from(networkExecutor))
            .flatMap {
              Observable.just(ResponseIntentListing(
                       action = it.result.action.substringBeforeLast("."),
                       parameters = it.result.parameters
               ))}
            .share()


     val devicePowerStateSetAction:Observable<ResponseParametersListing> =  dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_SWITCH_SET_ACTION_KEY }
            .flatMap {
                    val state = it.parameters[ChatBotProfile.DEVICE_STATE_PARAMETER_KEY]?.asString!!
                    val devices = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asJsonArray
                    val mapped = devices!!.map{
                        ResponseParametersListing(
                                    device = ChatBotProfile.Device.valueOf(it.asString),
                                    powerState = ChatBotProfile.State.valueOf(ChatBotProfile.parameterValueMapper(state))
                                                )
                        }.subList(0,devices.size())
                        Observable.fromIterable(mapped)
                    }
            .share()


     val devicePowerStateCheckAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_SWITCH_CHECK_ACTION_KEY }
            .flatMap {
                val devices = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asJsonArray

                val mapped = devices!!.map{
                    ResponseParametersListing(
                            device = ChatBotProfile.Device.valueOf(it.asString)
                    )
                }.subList(0,devices.size())
                Observable.fromIterable(mapped)
            }
            .share()


    val deviceBrightnessSetAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_BRIGHTNESS_SET_ACTION_KEY }
            .flatMap {
                val luminosityLevel = it.parameters[ChatBotProfile.DEVICE_BRIGHTNESS_PARAMETER_KEY]?.asString!!
                val devices = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asJsonArray
                val mapped = devices!!.map{
                    ResponseParametersListing(
                            device = ChatBotProfile.Device.valueOf(it.asString),
                            luminosity = ChatBotProfile.Luminosity.valueOf(ChatBotProfile.parameterValueMapper(luminosityLevel))
                    )
                }.subList(0,devices.size())
                Observable.fromIterable(mapped)
            }
            .share()

    val deviceBrightnessCheckAction:Observable<ResponseParametersListing> = dialogIntentsDispatcher.observeOn(Schedulers.from(networkExecutor))
            .filter{
                it.action == ChatBotProfile.DEVICE_BRIGHTNESS_CHECK_ACTION_KEY }
            .flatMap {
                val devices = it.parameters[ChatBotProfile.DEVICE_NAME_PARAMETER_KEY]?.asJsonArray

                val mapped = devices!!.map{
                    ResponseParametersListing(
                            device = ChatBotProfile.Device.valueOf(it.asString)
                    )
                }.subList(0,devices.size())
                Observable.fromIterable(mapped)
            }
            .share()
}