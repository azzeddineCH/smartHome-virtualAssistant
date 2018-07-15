package com.cerist.summer.virtualassistant.Views.Activities


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import com.cerist.summer.virtualassistant.Utils.getViewModel
import com.cerist.summer.virtualassistant.R
import com.cerist.summer.virtualassistant.Utils.BaseRecognitionActivity
import com.cerist.summer.virtualassistant.Views.Fragments.DialogFragment
import com.cerist.summer.virtualassistant.Utils.Repositories
import com.cerist.summer.virtualassistant.ViewModels.AirConditionerViewModel
import com.cerist.summer.virtualassistant.ViewModels.DialogViewModel
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import com.cerist.summer.virtualassistant.ViewModels.TvViewModel
import java.util.*

class HomeActivity:BaseRecognitionActivity(),
        DialogFragment.AudioRecordingButtonTouchListener,
        TextToSpeech.OnInitListener{


    companion object {
        val TAG = "HomeActivity"
    }

    private lateinit var mLampViewModel:LampViewModel
    private lateinit var mTvViewModel:TvViewModel
    private lateinit var mAirConditionerViewModel:AirConditionerViewModel
    private lateinit var mDialogViewModel: DialogViewModel
    private val mSpeechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(this)
    }
    private val mSpeechRecognizerIntent by lazy{
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    }
    private lateinit var  mTextToSpeech:TextToSpeech


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        mDialogViewModel = getViewModel(this,Repositories.DIALOG_REPOSITORY) as DialogViewModel
        mLampViewModel  = getViewModel(this,Repositories.LAMP_REPOSITORY) as LampViewModel
        mTvViewModel  = getViewModel(this,Repositories.TV_REPOSITORY) as TvViewModel
        mAirConditionerViewModel = getViewModel(this,Repositories.AIR_CONDITIONER_REPOSITORY) as AirConditionerViewModel

    }

    override fun onStart() {
        super.onStart()


        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container,DialogFragment())
                .commit()

        mTextToSpeech =  TextToSpeech(this,this)
        with(mSpeechRecognizerIntent){
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        mSpeechRecognizer.setRecognitionListener(this)

        mDialogViewModel.getTextResponse().observe(this, Observer {
            mTextToSpeech.speak(it,TextToSpeech.QUEUE_FLUSH,null,it?.hashCode().toString())
        })
        mDialogViewModel.getDevicePowerStateSetAction().observe(this, Observer {
            Log.d(TAG,"change the state of ${it?.device?.name}")
        })





    }

    override fun onAudioRecordingButtonTouch(action:Int) {
        Log.d(TAG,"onAudioRecordingButtonTouch")
      if(action == MotionEvent.ACTION_DOWN)
          mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
      else if (action == MotionEvent.ACTION_UP)
          mSpeechRecognizer.stopListening()
    }

    override fun onResults(results: Bundle?) {
        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
        mDialogViewModel.setDialogTextRequest(text!!)

    }

    override fun onInit(status: Int) {
        Log.d(TAG,"onInit")
        if(status == TextToSpeech.SUCCESS){
                 mTextToSpeech.language = Locale.UK
        }
    }




}