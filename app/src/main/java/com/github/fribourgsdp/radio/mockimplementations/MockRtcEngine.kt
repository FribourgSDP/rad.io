package com.github.fribourgsdp.radio.mockimplementations

import io.agora.rtc.*
import io.agora.rtc.audio.AudioRecordingConfiguration
import io.agora.rtc.internal.EncryptionConfig
import io.agora.rtc.internal.LastmileProbeConfig
import io.agora.rtc.live.LiveInjectStreamConfig
import io.agora.rtc.live.LiveTranscoding
import io.agora.rtc.mediaio.IVideoSink
import io.agora.rtc.mediaio.IVideoSource
import io.agora.rtc.models.ChannelMediaOptions
import io.agora.rtc.models.ClientRoleOptions
import io.agora.rtc.models.DataStreamConfig
import io.agora.rtc.models.UserInfo
import io.agora.rtc.video.*
import java.util.ArrayList

class MockRtcEngine: RtcEngine() {
    override fun setChannelProfile(profile: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setClientRole(role: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setClientRole(role: Int, options: ClientRoleOptions?): Int {
        TODO("Not yet implemented")
    }

    override fun sendCustomReportMessage(
        id: String?,
        category: String?,
        event: String?,
        label: String?,
        value: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun joinChannel(
        token: String?,
        channelName: String?,
        optionalInfo: String?,
        optionalUid: Int
    ): Int {
        return 0
    }

    override fun joinChannel(
        token: String?,
        channelName: String?,
        optionalInfo: String?,
        optionalUid: Int,
        options: ChannelMediaOptions?
    ): Int {
        return 0
    }

    override fun switchChannel(token: String?, channelName: String?): Int {
        TODO("Not yet implemented")
    }

    override fun switchChannel(
        token: String?,
        channelName: String?,
        options: ChannelMediaOptions?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun leaveChannel(): Int {
        TODO("Not yet implemented")
    }

    override fun renewToken(token: String?): Int {
        TODO("Not yet implemented")
    }

    override fun registerLocalUserAccount(appId: String?, userAccount: String?): Int {
        TODO("Not yet implemented")
    }

    override fun joinChannelWithUserAccount(
        token: String?,
        channelName: String?,
        userAccount: String?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun joinChannelWithUserAccount(
        token: String?,
        channelName: String?,
        userAccount: String?,
        options: ChannelMediaOptions?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun setCloudProxy(proxyType: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getUserInfoByUserAccount(userAccount: String?, userInfo: UserInfo?): Int {
        TODO("Not yet implemented")
    }

    override fun getUserInfoByUid(uid: Int, userInfo: UserInfo?): Int {
        TODO("Not yet implemented")
    }

    override fun enableWebSdkInteroperability(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun getConnectionState(): Int {
        TODO("Not yet implemented")
    }

    override fun enableRemoteSuperResolution(uid: Int, enable: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun enableAudio(): Int {
        TODO("Not yet implemented")
    }

    override fun disableAudio(): Int {
        TODO("Not yet implemented")
    }

    override fun pauseAudio(): Int {
        TODO("Not yet implemented")
    }

    override fun resumeAudio(): Int {
        TODO("Not yet implemented")
    }

    override fun setAudioProfile(profile: Int, scenario: Int): Int {
        return 0
    }

    override fun setHighQualityAudioParameters(
        fullband: Boolean,
        stereo: Boolean,
        fullBitrate: Boolean
    ): Int {
        TODO("Not yet implemented")
    }

    override fun adjustRecordingSignalVolume(volume: Int): Int {
        TODO("Not yet implemented")
    }

    override fun adjustPlaybackSignalVolume(volume: Int): Int {
        TODO("Not yet implemented")
    }

    override fun enableAudioVolumeIndication(interval: Int, smooth: Int, report_vad: Boolean): Int {
        return 0
    }

    override fun enableAudioQualityIndication(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun enableLocalAudio(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun muteLocalAudioStream(muted: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun muteRemoteAudioStream(uid: Int, muted: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun adjustUserPlaybackSignalVolume(uid: Int, volume: Int): Int {
        TODO("Not yet implemented")
    }

    override fun muteAllRemoteAudioStreams(muted: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setDefaultMuteAllRemoteAudioStreams(muted: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun enableVideo(): Int {
        TODO("Not yet implemented")
    }

    override fun disableVideo(): Int {
        TODO("Not yet implemented")
    }

    override fun setVideoProfile(profile: Int, swapWidthAndHeight: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setVideoProfile(width: Int, height: Int, frameRate: Int, bitrate: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setVideoEncoderConfiguration(config: VideoEncoderConfiguration?): Int {
        TODO("Not yet implemented")
    }

    override fun setCameraCapturerConfiguration(config: CameraCapturerConfiguration?): Int {
        TODO("Not yet implemented")
    }

    override fun setupLocalVideo(local: VideoCanvas?): Int {
        TODO("Not yet implemented")
    }

    override fun setupRemoteVideo(remote: VideoCanvas?): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalRenderMode(renderMode: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalRenderMode(renderMode: Int, mirrorMode: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setRemoteRenderMode(uid: Int, renderMode: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setRemoteRenderMode(uid: Int, renderMode: Int, mirrorMode: Int): Int {
        TODO("Not yet implemented")
    }

    override fun startPreview(): Int {
        TODO("Not yet implemented")
    }

    override fun stopPreview(): Int {
        TODO("Not yet implemented")
    }

    override fun enableLocalVideo(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun muteLocalVideoStream(muted: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun muteRemoteVideoStream(uid: Int, muted: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun muteAllRemoteVideoStreams(muted: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setDefaultMuteAllRemoteVideoStreams(muted: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setBeautyEffectOptions(enabled: Boolean, options: BeautyOptions?): Int {
        TODO("Not yet implemented")
    }

    override fun enableVirtualBackground(
        enabled: Boolean,
        backgroundSource: VirtualBackgroundSource?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun setDefaultAudioRoutetoSpeakerphone(defaultToSpeaker: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setEnableSpeakerphone(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun isSpeakerphoneEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun enableInEarMonitoring(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setInEarMonitoringVolume(volume: Int): Int {
        TODO("Not yet implemented")
    }

    override fun useExternalAudioDevice(): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalVoicePitch(pitch: Double): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalVoiceEqualization(bandFrequency: Int, bandGain: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalVoiceReverb(reverbKey: Int, value: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalVoiceChanger(voiceChanger: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalVoiceReverbPreset(preset: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setAudioEffectPreset(preset: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setVoiceBeautifierPreset(preset: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setVoiceConversionPreset(preset: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setAudioEffectParameters(preset: Int, param1: Int, param2: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setVoiceBeautifierParameters(preset: Int, param1: Int, param2: Int): Int {
        TODO("Not yet implemented")
    }

    override fun enableDeepLearningDenoise(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun enableSoundPositionIndication(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setRemoteVoicePosition(uid: Int, pan: Double, gain: Double): Int {
        TODO("Not yet implemented")
    }

    override fun startAudioMixing(
        filePath: String?,
        loopback: Boolean,
        replace: Boolean,
        cycle: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun startAudioMixing(
        filePath: String?,
        loopback: Boolean,
        replace: Boolean,
        cycle: Int,
        startPos: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun stopAudioMixing(): Int {
        TODO("Not yet implemented")
    }

    override fun pauseAudioMixing(): Int {
        TODO("Not yet implemented")
    }

    override fun resumeAudioMixing(): Int {
        TODO("Not yet implemented")
    }

    override fun adjustAudioMixingVolume(volume: Int): Int {
        TODO("Not yet implemented")
    }

    override fun adjustAudioMixingPlayoutVolume(volume: Int): Int {
        TODO("Not yet implemented")
    }

    override fun adjustAudioMixingPublishVolume(volume: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getAudioMixingPlayoutVolume(): Int {
        TODO("Not yet implemented")
    }

    override fun getAudioMixingPublishVolume(): Int {
        TODO("Not yet implemented")
    }

    override fun getAudioMixingDuration(): Int {
        TODO("Not yet implemented")
    }

    override fun getAudioMixingDuration(filePath: String?): Int {
        TODO("Not yet implemented")
    }

    override fun getAudioMixingCurrentPosition(): Int {
        TODO("Not yet implemented")
    }

    override fun setAudioMixingPosition(pos: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setAudioMixingPitch(pitch: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getAudioEffectManager(): IAudioEffectManager {
        TODO("Not yet implemented")
    }

    override fun startAudioRecording(filePath: String?, quality: Int): Int {
        TODO("Not yet implemented")
    }

    override fun startAudioRecording(filePath: String?, sampleRate: Int, quality: Int): Int {
        TODO("Not yet implemented")
    }

    override fun startAudioRecording(config: AudioRecordingConfiguration?): Int {
        TODO("Not yet implemented")
    }

    override fun stopAudioRecording(): Int {
        TODO("Not yet implemented")
    }

    override fun startEchoTest(): Int {
        TODO("Not yet implemented")
    }

    override fun startEchoTest(intervalInSeconds: Int): Int {
        TODO("Not yet implemented")
    }

    override fun stopEchoTest(): Int {
        TODO("Not yet implemented")
    }

    override fun enableLastmileTest(): Int {
        TODO("Not yet implemented")
    }

    override fun disableLastmileTest(): Int {
        TODO("Not yet implemented")
    }

    override fun startLastmileProbeTest(config: LastmileProbeConfig?): Int {
        TODO("Not yet implemented")
    }

    override fun stopLastmileProbeTest(): Int {
        TODO("Not yet implemented")
    }

    override fun setVideoSource(source: IVideoSource?): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalVideoRenderer(render: IVideoSink?): Int {
        TODO("Not yet implemented")
    }

    override fun setRemoteVideoRenderer(uid: Int, render: IVideoSink?): Int {
        TODO("Not yet implemented")
    }

    override fun setExternalAudioSink(enabled: Boolean, sampleRate: Int, channels: Int): Int {
        TODO("Not yet implemented")
    }

    override fun pullPlaybackAudioFrame(data: ByteArray?, lengthInByte: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setExternalAudioSource(enabled: Boolean, sampleRate: Int, channels: Int): Int {
        TODO("Not yet implemented")
    }

    override fun pushExternalAudioFrame(data: ByteArray?, timestamp: Long): Int {
        TODO("Not yet implemented")
    }

    override fun setExternalVideoSource(enable: Boolean, useTexture: Boolean, pushMode: Boolean) {
        TODO("Not yet implemented")
    }

    override fun pushExternalVideoFrame(frame: AgoraVideoFrame?): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTextureEncodeSupported(): Boolean {
        TODO("Not yet implemented")
    }

    override fun registerAudioFrameObserver(observer: IAudioFrameObserver?): Int {
        TODO("Not yet implemented")
    }

    override fun registerVideoEncodedFrameObserver(observer: IVideoEncodedFrameObserver?): Int {
        TODO("Not yet implemented")
    }

    override fun registerVideoFrameObserver(observer: IVideoFrameObserver?): Int {
        TODO("Not yet implemented")
    }

    override fun setRecordingAudioFrameParameters(
        sampleRate: Int,
        channel: Int,
        mode: Int,
        samplesPerCall: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun setPlaybackAudioFrameParameters(
        sampleRate: Int,
        channel: Int,
        mode: Int,
        samplesPerCall: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun setMixedAudioFrameParameters(sampleRate: Int, samplesPerCall: Int): Int {
        TODO("Not yet implemented")
    }

    override fun addVideoWatermark(watermark: AgoraImage?): Int {
        TODO("Not yet implemented")
    }

    override fun addVideoWatermark(watermarkUrl: String?, options: WatermarkOptions?): Int {
        TODO("Not yet implemented")
    }

    override fun clearVideoWatermarks(): Int {
        TODO("Not yet implemented")
    }

    override fun setRemoteUserPriority(uid: Int, userPriority: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalPublishFallbackOption(option: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setRemoteSubscribeFallbackOption(option: Int): Int {
        TODO("Not yet implemented")
    }

    override fun enableDualStreamMode(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setRemoteVideoStreamType(uid: Int, streamType: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setRemoteDefaultVideoStreamType(streamType: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setEncryptionSecret(secret: String?): Int {
        TODO("Not yet implemented")
    }

    override fun setEncryptionMode(encryptionMode: String?): Int {
        TODO("Not yet implemented")
    }

    override fun enableEncryption(enabled: Boolean, config: EncryptionConfig?): Int {
        TODO("Not yet implemented")
    }

    override fun addInjectStreamUrl(url: String?, config: LiveInjectStreamConfig?): Int {
        TODO("Not yet implemented")
    }

    override fun removeInjectStreamUrl(url: String?): Int {
        TODO("Not yet implemented")
    }

    override fun addPublishStreamUrl(url: String?, transcodingEnabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun removePublishStreamUrl(url: String?): Int {
        TODO("Not yet implemented")
    }

    override fun setLiveTranscoding(transcoding: LiveTranscoding?): Int {
        TODO("Not yet implemented")
    }

    override fun createDataStream(reliable: Boolean, ordered: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun createDataStream(config: DataStreamConfig?): Int {
        TODO("Not yet implemented")
    }

    override fun sendStreamMessage(streamId: Int, message: ByteArray?): Int {
        TODO("Not yet implemented")
    }

    override fun setVideoQualityParameters(preferFrameRateOverImageQuality: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setLocalVideoMirrorMode(mode: Int): Int {
        TODO("Not yet implemented")
    }

    override fun switchCamera(): Int {
        TODO("Not yet implemented")
    }

    override fun isCameraZoomSupported(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCameraTorchSupported(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCameraFocusSupported(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCameraExposurePositionSupported(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCameraAutoFocusFaceModeSupported(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setCameraZoomFactor(factor: Float): Int {
        TODO("Not yet implemented")
    }

    override fun getCameraMaxZoomFactor(): Float {
        TODO("Not yet implemented")
    }

    override fun setCameraFocusPositionInPreview(positionX: Float, positionY: Float): Int {
        TODO("Not yet implemented")
    }

    override fun setCameraExposurePosition(positionXinView: Float, positionYinView: Float): Int {
        TODO("Not yet implemented")
    }

    override fun enableFaceDetection(enable: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setCameraTorchOn(isOn: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun setCameraAutoFocusFaceModeEnabled(enabled: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun getCallId(): String {
        TODO("Not yet implemented")
    }

    override fun rate(callId: String?, rating: Int, description: String?): Int {
        TODO("Not yet implemented")
    }

    override fun complain(callId: String?, description: String?): Int {
        TODO("Not yet implemented")
    }

    override fun setLogFile(filePath: String?): Int {
        TODO("Not yet implemented")
    }

    override fun setLogFilter(filter: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setLogFileSize(fileSizeInKBytes: Int): Int {
        TODO("Not yet implemented")
    }

    override fun uploadLogFile(): String {
        TODO("Not yet implemented")
    }

    override fun getNativeHandle(): Long {
        TODO("Not yet implemented")
    }

    override fun enableHighPerfWifiMode(enable: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun monitorHeadsetEvent(monitor: Boolean) {
        TODO("Not yet implemented")
    }

    override fun monitorBluetoothHeadsetEvent(monitor: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setPreferHeadset(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setParameters(parameters: String?): Int {
        TODO("Not yet implemented")
    }

    override fun getParameter(parameter: String?, args: String?): String {
        TODO("Not yet implemented")
    }

    override fun registerMediaMetadataObserver(observer: IMetadataObserver?, type: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setLogWriter(logWriter: ILogWriter?): Int {
        TODO("Not yet implemented")
    }

    override fun releaseLogWriter(): Int {
        TODO("Not yet implemented")
    }

    override fun startChannelMediaRelay(channelMediaRelayConfiguration: ChannelMediaRelayConfiguration?): Int {
        TODO("Not yet implemented")
    }

    override fun stopChannelMediaRelay(): Int {
        TODO("Not yet implemented")
    }

    override fun updateChannelMediaRelay(channelMediaRelayConfiguration: ChannelMediaRelayConfiguration?): Int {
        TODO("Not yet implemented")
    }

    override fun startDumpVideoReceiveTrack(uid: Int, dumpFile: String?): Int {
        TODO("Not yet implemented")
    }

    override fun stopDumpVideoReceiveTrack(): Int {
        TODO("Not yet implemented")
    }

    override fun createRtcChannel(channelId: String?): RtcChannel {
        TODO("Not yet implemented")
    }

    override fun setLocalAccessPoint(ips: ArrayList<String>?, domain: String?): Int {
        TODO("Not yet implemented")
    }
}