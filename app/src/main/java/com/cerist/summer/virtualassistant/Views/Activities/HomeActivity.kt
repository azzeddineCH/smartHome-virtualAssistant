package com.cerist.summer.virtualassistant.Views.Activities


import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Entities.ChatBotProfile
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Utils.Functions.getViewModel
import com.cerist.summer.virtualassistant.R
import com.cerist.summer.virtualassistant.Utils.BaseComponents.BaseRecognitionActivity
import com.cerist.summer.virtualassistant.Utils.Functions.getStringResourceByName
import com.cerist.summer.virtualassistant.Views.Fragments.DialogFragment
import com.cerist.summer.virtualassistant.Utils.Repositories
import com.cerist.summer.virtualassistant.ViewModels.AirConditionerViewModel
import com.cerist.summer.virtualassistant.ViewModels.DialogViewModel
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import com.cerist.summer.virtualassistant.ViewModels.TvViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import java.util.*

class HomeActivity: BaseRecognitionActivity(),
        DialogFragment.AudioRecordingButtonTouchListener,
        TextToSpeech.OnInitListener{


    companion object {
        val TAG = "HomeActivity"
    }

    private lateinit var mLampViewModel:LampViewModel
    private lateinit var mTvViewModel:TvViewModel
    private lateinit var mAirConditionerViewModel:AirConditionerViewModel
    private lateinit var mDialogViewModel: DialogViewModel

    private lateinit var mTextToSpeech:TextToSpeech
    private lateinit var mSpeechRecognizer: SpeechRecognizer
    private lateinit var mSpeechRecognizerIntent :Intent
    private lateinit var rxPermissions:RxPermissions

    private lateinit var disposable:Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onStart")
        setContentView(R.layout.home_activity)



        mDialogViewModel = getViewModel(this,Repositories.DIALOG_REPOSITORY) as DialogViewModel
        mLampViewModel  = getViewModel(this,Repositories.LAMP_REPOSITORY) as LampViewModel
        mTvViewModel  = getViewModel(this,Repositories.TV_REPOSITORY) as TvViewModel
        mAirConditionerViewModel = getViewModel(this,Repositories.AIR_CONDITIONER_REPOSITORY) as AirConditionerViewModel
        rxPermissions  = RxPermissions(this)


        disposable = rxPermissions.requestEach(
                     Manifest.permission.ACCESS_COARSE_LOCATION,
                     Manifest.permission.ACCESS_FINE_LOCATION,
                     Manifest.permission.RECORD_AUDIO)
                      .subscribe {

                    }


        /**
         * Observing the lamp luminosity and power state characteristics
         */

        mLampViewModel.getLampPowerStateLiveData().observe(this, Observer{
            Log.d(TAG,"subscribing to the lamp power state changes")
            mTextToSpeech.speak("${getString(R.string.lamp_power_state_indicator)} $it",TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())
        })

        mLampViewModel.getLampLuminosityLevelLiveData().observe(this, Observer{
            Log.d(TAG,"subscribing to the lamp luminosity change")
            mTextToSpeech.speak("${getString(R.string.lamp_mode_indicator)} $it",TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())

        })


        mLampViewModel.getLampConnectionErrorLiveData().observe(this, Observer {
            Log.d(TAG,"subscribing to the lamp errors changes")
            mTextToSpeech.speak(getStringResourceByName(it!!),TextToSpeech.QUEUE_ADD,null,it.hashCode().toString())
        })


        /**
         * Observing the TV volume and power state characteristics
         */

        mTvViewModel.getTvPowerStateLiveDate().observe(this, Observer {
            Log.d(TAG,"subscribing to the tv power state changes")
            mTextToSpeech.speak("${getString(R.string.tv_state_indicator)} $it",TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())

        })

        mTvViewModel.getTvVolumeLevelLiveData().observe(this, Observer {
            Log.d(TAG,"subscribing to the tv volume changes")
            mTextToSpeech.speak("${getString(R.string.tv_volume_indicator)} $it",TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())

        })

        mTvViewModel.getTvTimerLiveData().observe(this, Observer {
            Log.d(TAG,"subscribing to the tv timer changes")
            mTextToSpeech.speak("${getString(R.string.tv_timer_indicator)} $it",TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())

        })

        mTvViewModel.getTvConnectionErrorLiveData().observe(this, Observer {
            Log.d(TAG,"subscribing to the tv errors changes")
            mTextToSpeech.speak(getStringResourceByName(it!!),TextToSpeech.QUEUE_ADD,null,it.hashCode().toString())

        })




        /**
         * Observing the Air conditioner temperature, mode and power state characteristics
         */

        mAirConditionerViewModel.getAirConditionerPowerStateLiveData().observe(this, Observer {
            Log.d(TAG,"subscribing to the air conditioner power state changes")
            mTextToSpeech.speak("${getString(R.string.air_conditioner_state_indicator)} $it",TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())
        })

        mAirConditionerViewModel.getAirConditionerModeLiveData().observe(this, Observer {
            Log.d(TAG,"subscribing to the air conditioner  mode changes")
            mTextToSpeech.speak("${getString(R.string.air_conditioner_mode_indicator)} $it",TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())
        })

        mAirConditionerViewModel.getAirConditionerTempLiveData().observe(this, Observer {
            Log.d(TAG,"subscribing to the air conditioner  temperature changes")
            mTextToSpeech.speak("${getString(R.string.air_conditioner_temp_indicator)} $it",TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())
        })

        mAirConditionerViewModel.getAirConditionerConnectionErrorLiveData().observe(this, Observer {
            Log.d(TAG,"subscribing to the tv errors changes")
            mTextToSpeech.speak(getStringResourceByName(it!!),TextToSpeech.QUEUE_ADD,null,it.hashCode().toString())
        })


        /**
         * Observing the BOT text responses
         */

        mDialogViewModel.getTextResponse().observe(this, Observer {
            Log.d(TAG,"subscribing to the text response changes")
            mTextToSpeech.speak(it,TextToSpeech.QUEUE_ADD,null,it?.hashCode().toString())
        })

        mDialogViewModel.getDialogErrorStatus().observe(this, Observer {
            Log.d(TAG,"subscribing to the text response changes")
            mTextToSpeech.speak(getStringResourceByName(it!!),TextToSpeech.QUEUE_ADD,null,it.hashCode().toString())
        })


        /**
         * Observing the BOT responses to the device power state actions
         */

        mDialogViewModel.getDevicePowerStateSetAction().observe(this, Observer {
            Log.d(TAG,"subscribing to the power dialog")

            val device = it?.device
            val state  = it?.powerState !!
            when(device){
                ChatBotProfile.Device.TV -> mTvViewModel.setTvPowerState(BroadLinkProfile.TvProfile.State.valueOf(state.name))
                ChatBotProfile.Device.AIR_CONDITIONER -> mAirConditionerViewModel.setAirConditionerPowerState(BroadLinkProfile.AirConditionerProfile.State.valueOf(state.name))
                ChatBotProfile.Device.LAMP -> mLampViewModel.setLampPowerState(LampProfile.State.valueOf(state.name))
            }
        })

        mDialogViewModel.getDevicePowerStateCheckAction().observe(this, Observer {
            Log.d(TAG,"subscribing to the  power state check action")
            val device = it?.device
            when(device){
                ChatBotProfile.Device.TV -> mTvViewModel.getTvPowerState()
                ChatBotProfile.Device.AIR_CONDITIONER -> mAirConditionerViewModel.getAirConditionerPowerState()
                ChatBotProfile.Device.LAMP -> mLampViewModel.getLampPowerState()
            }
        })


        /**
         * Observing the BOT responses to the device brightness actions
         */

        mDialogViewModel.getDeviceBrightnessCheckAction().observe(this, Observer {
            mLampViewModel.getLampLuminosityLevel()
        })

        mDialogViewModel.getDeviceBrightnessSetAction().observe(this, Observer {
            val level = it?.luminosity !!
            Log.d(TAG,"subscribing to the lamp luminosity set action: $level")
            mLampViewModel.setLampLuminosityLevel(LampProfile.Luminosity.valueOf(level.name))
        })

        /**
         * Observing the BOT responses to the device mode actions
         */

        mDialogViewModel.getDeviceModeSetAction().observe(this, Observer {
            val mode = it?.airMode !!
            Log.d(TAG,"subscribing to the air conditioner mode check action : $mode")
            mAirConditionerViewModel.setAirConditionerMode(BroadLinkProfile.AirConditionerProfile.Mode.valueOf(mode.name))

        })

        mDialogViewModel.getDeviceModeCheckAction().observe(this, Observer {
            mAirConditionerViewModel.getAirConditionerMode()
        })


        /**
         * Observing the BOT responses to the device volume actions
         */

        mDialogViewModel.getDeviceVolumeSetAction().observe(this, Observer {
            Log.d(TAG,"subscribing to the TV volume set action")

            val volume = it?.volume !!
            mTvViewModel.setTvVolumeLevel(volume)

        })

        mDialogViewModel.getDeviceVolumeCheckAction().observe(this, Observer {
            Log.d(TAG,"subscribing to the TV volume check action")
             mTvViewModel.getTvVolumeLevel()
        })


        /**
         * Observing the BOT responses to the device timer actions
         */

        mDialogViewModel.getDeviceTimerSetAction().observe(this, Observer {
            Log.d(TAG,"subscribing to the TV volume set action")
            val time = it?.timer !!
            mTvViewModel.setTvTimer(time)

        })








    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart")

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container,DialogFragment())
                .commit()


        /**
         * setting up the (text to speech)/(speech to text)
         */

        mSpeechRecognizer =  SpeechRecognizer.createSpeechRecognizer(this)
        mSpeechRecognizerIntent =   Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        mTextToSpeech =  TextToSpeech(this,this)
        with(mSpeechRecognizerIntent){
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,500)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,500)
        }
        mSpeechRecognizer.setRecognitionListener(this)


    }

    override fun onAudioRecordingButtonTouch() {
        Log.d(TAG,"onAudioRecordingButtonTouch")
          mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
    }

    override fun onResults(results: Bundle?) {
        Log.d(TAG,"onAudioRecordingButtonTouch:")
        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) !!
        mDialogViewModel.setDialogTextRequest(text)

    }


    override fun onInit(status: Int) {
        Log.d(TAG,"onInit")
        if(status == TextToSpeech.SUCCESS){
                 mTextToSpeech.language = Locale.US
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        mTextToSpeech.stop()
        mTextToSpeech.shutdown()
        mSpeechRecognizer.stopListening()
        mSpeechRecognizer.destroy()

        disposable.dispose()
    }
}