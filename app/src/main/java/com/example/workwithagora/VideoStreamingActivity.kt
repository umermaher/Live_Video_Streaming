package com.example.workwithagora

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.workwithagora.databinding.ActivityVideoStreamingBinding
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas

class VideoStreamingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoStreamingBinding
    private var channelName:String?=null
    private var userRole:Int?=null
    private var rtcEngine:RtcEngine?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVideoStreamingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        channelName=intent.getStringExtra(CHANNEL_NAME)
        userRole=intent.getIntExtra(USER_ROlE,-1)

        initAgoraEngineAndJoinChannel()

        Toast.makeText(this,"$channelName, $userRole",Toast.LENGTH_LONG).show()

        binding.micBtn.setOnClickListener {
            val iv=it as ImageView
            if(iv.isSelected){
                iv.isSelected=false
                iv.clearColorFilter()
            }else{
                iv.isSelected=true
                iv.setColorFilter(resources.getColor(R.color.purple_700),PorterDuff.Mode.MULTIPLY)
            }
            rtcEngine?.muteLocalAudioStream(iv.isSelected)
        }

        binding.switchCameraBtn.setOnClickListener {
            rtcEngine?.switchCamera()
        }

        binding.endCallBtn.setOnClickListener { finish() }
    }

    private fun initAgoraEngineAndJoinChannel() {
        initAgoraEngine()

        rtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        rtcEngine?.setClientRole(userRole!!)
        rtcEngine?.enableVideo()

        if(userRole==1)
            setUpLocalVideo()
        else
            binding.localVideoViewContainer.visibility=View.GONE

        joinChannel()
    }

    private val mRtcEventHandler=object : IRtcEngineEventHandler(){
        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.i("VideoStreamingActivity","Join user success : $uid")
            runOnUiThread { setUpRemoteVideo(uid) }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread { onRemoteUserLeft() }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            Log.i("VideoStreamingActivity","Join channel success : $uid")
        }
    }
    private fun initAgoraEngine() {
        try{
            rtcEngine=RtcEngine.create(baseContext, APP_ID,mRtcEventHandler)
        }catch(e:Exception){
            Log.e("VideoStreamingActivity",e.message.toString())
        }
    }
    private fun setUpLocalVideo(){
        val surfaceView=RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        binding.localVideoViewContainer.addView(surfaceView)
        rtcEngine?.setupLocalVideo(VideoCanvas(surfaceView,VideoCanvas.RENDER_MODE_FIT,0))
    }
    private fun joinChannel(){
        rtcEngine?.joinChannel(token,channelName,null,0)
    }
    private fun setUpRemoteVideo(uid:Int){
        if(binding.remoteVideoViewContainer.childCount>=1) return
        val surfaceView=RtcEngine.CreateRendererView(baseContext)
      //  surfaceView.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(surfaceView)
        rtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView,VideoCanvas.RENDER_MODE_FIT,uid))
        surfaceView.tag=uid
    }

    private fun onRemoteUserLeft(){
        binding.remoteVideoViewContainer.removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine=null
    }
}