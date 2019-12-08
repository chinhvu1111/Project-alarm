package com.e15.alarmnats.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.e15.alarmnats.Model.Task
import com.e15.alarmnats.R
import com.e15.alarmnats.activity.TimerActivity
import com.e15.alarmnats.databinding.FragmentTimerBinding
import com.e15.alarmnats.service.PomodoroService
import com.e15.alarmnats.utils.InjectorUtils
import com.e15.alarmnats.viewmodel.PomodoroViewModel
import java.text.SimpleDateFormat
import java.util.*

class PomodoroFragment : Fragment() {

    private var mBinding: FragmentTimerBinding? = null
    private var pomodoroViewModel: PomodoroViewModel? = null
    private var animatorOut: AnimatorSet? = null
    private var animatorIn: AnimatorSet? = null
    private var pomodoroListener: PomodoroListener? = null

    private val animOutListener = object : Animator.AnimatorListener {
        @SuppressLint("RestrictedApi")
        override fun onAnimationStart(animator: Animator) {

            pomodoroViewModel!!.setStopButtonVisibility(true)

            pomodoroViewModel!!.setStopButtonClickable(false)

            pomodoroViewModel!!.setPlayPauseButtonClickable(false)

            mBinding!!.playPauseButton.setImageResource(R.drawable.ic_pause_24dp)

        }

        override fun onAnimationEnd(animator: Animator) {

            pomodoroViewModel!!.setPlayPauseButtonClickable(true)

            pomodoroViewModel!!.setStopButtonClickable(true)

        }

        override fun onAnimationCancel(animator: Animator) {

        }

        override fun onAnimationRepeat(animator: Animator) {

        }
    }

    private val animInListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) {

            pomodoroViewModel!!.setPlayPauseButtonClickable(false)

            pomodoroViewModel!!.setStopButtonClickable(false)

            mBinding!!.playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp)

        }

        @SuppressLint("RestrictedApi")
        override fun onAnimationEnd(animator: Animator) {

            pomodoroViewModel!!.setStopButtonVisibility(false)

            pomodoroViewModel!!.setPlayPauseButtonClickable(true)

        }

        override fun onAnimationCancel(animator: Animator) {

        }

        override fun onAnimationRepeat(animator: Animator) {

        }
    }

    private val pomConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {}

        override fun onServiceDisconnected(name: ComponentName) {}

    }

    private fun initAnimations(initialX: Float) {

        //Move to started position
        val slideOut = ObjectAnimator.ofFloat(mBinding!!.playPauseButton, "translationX", initialX - 150f)
        slideOut.duration = 500
        slideOut.interpolator = AccelerateDecelerateInterpolator()

        val fadeIn: ObjectAnimator = ObjectAnimator.ofFloat(mBinding!!.stopButton, "alpha", 0f, 1f);
        fadeIn.setDuration(500)
        fadeIn.setInterpolator(AccelerateDecelerateInterpolator())

        val stopButtonMoveOut = ObjectAnimator.ofFloat(mBinding!!.stopButton, "translationX", initialX + 150f)
        stopButtonMoveOut.duration = 500
        stopButtonMoveOut.interpolator = AccelerateDecelerateInterpolator()

        //Move back to original position
        val slideIn = ObjectAnimator.ofFloat(mBinding!!.playPauseButton, "translationX", initialX)
        slideIn.duration = 500
        slideIn.interpolator = AccelerateDecelerateInterpolator()

        val stopButtonFadeOut: ObjectAnimator = ObjectAnimator.ofFloat(mBinding!!.stopButton, "alpha", 1f, 0f)
        stopButtonFadeOut.setDuration(500)
        stopButtonFadeOut.setInterpolator(AccelerateDecelerateInterpolator())

        val stopButtonMoveIn = ObjectAnimator.ofFloat(mBinding!!.stopButton, "translationX", initialX)
        stopButtonFadeOut.setDuration(500)
        stopButtonFadeOut.setInterpolator(AccelerateDecelerateInterpolator())

        //Setup animation sequence
        animatorOut = AnimatorSet()
        animatorOut!!.addListener(animOutListener)
        animatorOut!!.playTogether(slideOut, fadeIn, stopButtonMoveOut)

        animatorIn = AnimatorSet()
        animatorIn!!.addListener(animInListener)
        animatorIn!!.playTogether(slideIn, stopButtonFadeOut, stopButtonMoveIn)
    }

    interface PomodoroListener {
        fun connectService(serviceConnection: ServiceConnection)
        fun disconnectService(connection: ServiceConnection)
        fun publishAction(action: String, dataIntent: Intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        //Returns the outermost View in the layout file associated with the Binding.
        return mBinding!!.getRoot()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()

        setupListeners()

        initAnimations(mBinding!!.playPauseButton.getTranslationX())

    }

    override fun onStart() {

        super.onStart()

        pomodoroListener!!.connectService(pomConnection)

    }

    override fun onResume() {
        super.onResume()

        if (pomodoroViewModel!!.animationState) {

            animatorOut!!.start()

            if (pomodoroViewModel!!.timerRunning!!) {

                mBinding!!.playPauseButton.setImageResource(R.drawable.ic_pause_24dp)

            } else {

                mBinding!!.playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp)

            }
        }
    }

    override fun onStop() {

        super.onStop()

        pomodoroListener!!.disconnectService(pomConnection)

    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
        try {

            //Context: TimerActivity
            //Because this (fragment) is initialized such that it is attached to (root : TimerActivity)
            pomodoroListener = context as PomodoroListener?

        } catch (e: ClassCastException) {

            throw ClassCastException(e.toString() + "implement PomodoroListener")

        }

    }


    private fun initViewModel() {

        val parent = activity

        if (parent != null) {

            ///Factory is class used t manufacture (ViewModel)
            val factory = InjectorUtils.providePomodoroViewModelFactory()

            //ViewModelProviders.of()
            //Params:
            //activity – an activity, in whose scope (ViewModels) should be retained

            //factory – a Factory to instantiate new ViewModels

            //Returns:
            //a ViewModelProvider instance

            //get(ViewModel::class.java)
            //Returns an existing ViewModel or creates a new one in the scope (usually, a fragment or an activity),
            // associated with this ViewModelProvider.
            //The created ViewModel is associated with the given scope and will be retained
            // as long as the scope is alive (e.g. if it is an activity, until it is finished or process is killed).
            pomodoroViewModel = ViewModelProviders.of(parent, factory).get(PomodoroViewModel::class.java)

            //(Casting) here can be failed because (FragmentActivity) cannot be cast into (LifecycleOwner)

            //Sets the (LifecycleOwner) that should be used for observing changes of LiveData in this binding.
            // If a LiveData is in one of the binding expressions and no LifecycleOwner is set,
            // the LiveData will not be observed and updates to it will not be propagated to the UI.
            mBinding!!.setLifecycleOwner(parent as LifecycleOwner)

            //We also can bind (PomodoroViewModel) to (UI)
            mBinding!!.setViewModel(pomodoroViewModel)

        }
    }

    //to send (broadcast) to (TimerReceiver) class
    //It means this here relative to PomodoroService
    private fun setupListeners() {

        var task = Task(0);

        task.fromIntent(activity!!.intent);

        var newIntent= Intent()

        task.toIntent(newIntent);

        mBinding?.tvIntervalTime?.setText("Khung giờ tiêu chuẩn: "+task.startTime + " đến " + task.endTime)

        mBinding?.tvLabelOfTask?.setText(task.title)

        //Note stateData and sessionData --> Changing
        pomodoroViewModel!!.stateData.observe(this, Observer { state -> })

        pomodoroViewModel!!.sessionData.observe(this, Observer { state -> })

        pomodoroViewModel!!.currentEndTime.observe(this, Observer { endTime-> })

        pomodoroViewModel!!.currentLeftTimeMiliLong.observe(this,object:Observer<Long>{

            override fun onChanged(t: Long?) {

                TimerActivity.remainingTime=t!!

            }

        })

        pomodoroViewModel!!.tastIsDone.observe(this, object:Observer<String>{
            override fun onChanged(t: String?) {

                if(t.equals("Hoàn thành tác vụ")){

                    Toast.makeText(activity!!.applicationContext,"Đã hoàn thành tác vụ",Toast.LENGTH_SHORT).show()

                    AlertDialog.Builder(activity!!, R.style.MyDialogTheme).setMessage("Tác vụ đã hoàn thành bạn có muốn quay về giao diện chính?")
                            .setPositiveButton("Có", DialogInterface.OnClickListener { dialog, which ->

                                activity!!.onBackPressed()

                            }).setNegativeButton("Không",null).show()

                }

            }
        })

        var difference:Long=0

        if(task.remainingTime>0.toLong()){

            difference=task.remainingTime

        }else if(task.startTime==null&&task.endTime==null){

            difference=-1

        }else{
            var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm");

            var startTime = simpleDateFormat.parse(task.startTime);
            var endTime = simpleDateFormat.parse(task.endTime);

            difference = endTime.time - startTime.time
        }

        PomodoroService.intervalTime=difference.toDouble();

        //Seting action for playpauseButton
        mBinding!!.playPauseButton.setOnClickListener({ view ->

            var setting: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

            var sessionTime = setting.getInt("pref_work_time", 25);

            if (sessionTime * 60 * 1000 > difference&&difference!=-1.toLong()) {

//                Toast.makeText(activity, "Please setting working time again!", Toast.LENGTH_SHORT).show()

                Log.e("-----------------------",difference.toString())

                //Restart
                PomodoroService.intervalTime=difference.toDouble();

                //If clock is running, you (click button) to move (pause state)
                if (pomodoroViewModel!!.timerRunning!!) {

                    pomodoroListener!!.publishAction(PomodoroService.ACTION_PAUSE,newIntent)

                    mBinding!!.playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp)

                } else {
                    pomodoroListener!!.publishAction(PomodoroService.ACTION_START,newIntent)

                    mBinding!!.playPauseButton.setImageResource(R.drawable.ic_pause_24dp)

                    if (!pomodoroViewModel!!.animationState) {

                        pomodoroViewModel!!.animationState = true

                        animatorOut!!.start()

                    }
                }

            } else {

                //Log.e("-----------------------",difference.toString())

                if (pomodoroViewModel!!.timerRunning!!) {

                    //Here difference of the data is not sent
                    //First of all we need to startService
                    //Then we send broadcast to this service to execute some commands

                    pomodoroListener!!.publishAction(PomodoroService.ACTION_PAUSE,newIntent)

                    mBinding!!.playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp)

                } else {
                    pomodoroListener!!.publishAction(PomodoroService.ACTION_START, newIntent)

                    mBinding!!.playPauseButton.setImageResource(R.drawable.ic_pause_24dp)

                    if (!pomodoroViewModel!!.animationState) {

                        pomodoroViewModel!!.animationState = true

                        animatorOut!!.start()

                    }
                }
            }

        })

        mBinding!!.stopButton.setOnClickListener({ view ->

            pomodoroListener!!.publishAction(PomodoroService.ACTION_RESET,newIntent)

            pomodoroViewModel!!.animationState = false

            animatorIn!!.start()

        })

    }

    companion object {

        val instance: PomodoroFragment
            get() = PomodoroFragment()

    }

}