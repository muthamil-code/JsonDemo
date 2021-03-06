package com.tam.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_exercise.*
import java.util.*
import kotlin.collections.ArrayList


class ExerciseActivity : AppCompatActivity(),TextToSpeech.OnInitListener {

   private  var restTimer : CountDownTimer? = null
   private var resetProgress = 0

   private  var exerciseTimer : CountDownTimer? = null
   private  var exerciseProgress = 0
   private var exerciseDuration : Long = 30
    private var tts : TextToSpeech? = null

   private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        setSupportActionBar(tool_bar_exercise_id)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tool_bar_exercise_id.setNavigationOnClickListener{
                 onBackPressed()
        }
        setUpRestView()
        exerciseList = Constants.defaultExerciseList()
        tts = TextToSpeech(this,this)

    }

    override fun onDestroy() {
        if(restTimer != null){
            restTimer!!.cancel()
            resetProgress = 0
        }
        super.onDestroy()
    }

    private fun setRestProgress(){
        time_pro.progress = resetProgress
        restTimer = object : CountDownTimer(10000,1000){
            override fun onTick(p0: Long) {
                resetProgress++
                time_pro.progress = 10-resetProgress
                tvTimer.text = (10 - resetProgress).toString()

                upComeId.text = exerciseList!![currentExercisePosition].getName()

            }

            override fun onFinish() {


                setUpExerciseView()


            }
        }.start()

    }
    private fun setExerciseProgress(){
        time_pro_excersis.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseDuration * 1000,1000){
   