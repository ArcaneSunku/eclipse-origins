package dev.atomixsoft.solar_eclipse.client.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.openal.*;

import dev.atomixsoft.solar_eclipse.client.audio.files.Midi;
import dev.atomixsoft.solar_eclipse.client.audio.files.Wav;


/**
 * <p>A static class meant to load Music and Sound Effects for use with OpenAL. <br/>
 * It will initially support only WAV for Sound and MIDI for Music.</p>
 */
@SuppressWarnings("unused")
public class AudioMaster {
    private static AudioMaster ms_Instance = null;

    private final Map<String, Integer> m_BufferMap;

    private ALCapabilities m_alCapabilities;
    private ALCCapabilities m_alcCapabilities;

    private long m_Device, m_Context;


    private AudioMaster() {
        m_BufferMap = new HashMap<>();
    }

    public static AudioMaster Instance() {
        if(ms_Instance == null) ms_Instance = new AudioMaster();
        return ms_Instance;
    }

    /**
     * Initializes OpenAL my getting the device ready and setting up the context.
     */
    public static void Init() {
        AudioMaster instance = Instance();

        String defaultDevice = ALC11.alcGetString(0, ALC11.ALC_DEFAULT_DEVICE_SPECIFIER);
        if(defaultDevice == null) throw new RuntimeException("Failed to find a default audio device!");

        instance.m_Device = ALC11.alcOpenDevice(defaultDevice);
        instance.m_alcCapabilities = ALC.createCapabilities(instance.m_Device);

        instance.m_Context = ALC11.alcCreateContext(instance.m_Device, (IntBuffer) null);
        ALC11.alcMakeContextCurrent(instance.m_Context);

        instance.m_alCapabilities = AL.createCapabilities(instance.m_alcCapabilities);
    }

    /**
     * Cleans up our buffers before cleaning up OpenAL
     */
    public static void CleanUp() {
        AudioMaster instance = Instance();

        instance.m_BufferMap.values().forEach(AL11::alDeleteBuffers);
        instance.m_BufferMap.clear();

        ALC11.alcMakeContextCurrent(MemoryUtil.NULL);
        ALC11.alcDestroyContext(instance.m_Context);
        ALC11.alcCloseDevice(instance.m_Device);
    }

    /**
     * Load Music (MIDI) into an OpenAL buffer for use.
     *
     * @param name the name we'll store the buffer in the {@link Map} under
     * @param fileName the path to the midi file
     * @return a buffer ID to be used with {@link AudioSource}
     */
    public static int LoadMusic(String name, String fileName) {
        if(Instance().m_BufferMap.containsKey(name))
            return Instance().m_BufferMap.get(name);

        int buffer = AL11.alGenBuffers();
        ByteBuffer byteBuffer = null;
        try {
            Midi file = new Midi(fileName);

            byteBuffer = MemoryUtil.memAlloc(file.getData().length);
            byteBuffer.put(file.getData());

            int format = FileFormat(file.getChannels(), file.getSampleSize());
            AL11.alBufferData(buffer, format, byteBuffer.flip(), (int) file.getSampleRate());
        } finally {
            if(byteBuffer != null)
                MemoryUtil.memFree(byteBuffer);
        }

        Instance().m_BufferMap.put(name, buffer);

        return buffer;
    }

    /**
     * Load a Sound Effect (WAV) into an OpenAL buffer for use.
     *
     * @param name the name we'll store the buffer in the {@link Map} under
     * @param fileName the path to the .wav file
     * @return a buffer ID to be used with {@link AudioSource}
     */
    public static int LoadSound(String name, String fileName) {
        if(Instance().m_BufferMap.containsKey(name))
            return Instance().m_BufferMap.get(name);

        int buffer = AL11.alGenBuffers();
        ByteBuffer byteBuffer = null;
        try {
            Wav file = new Wav(fileName);

            byteBuffer = MemoryUtil.memAlloc(file.getData().length);
            byteBuffer.put(0, file.getData());

            int format = FileFormat(file.getChannels(), file.getSampleSize());
            AL11.alBufferData(buffer, format, byteBuffer, (int) file.getSampleRate());
        } finally {
            if(byteBuffer != null)
                MemoryUtil.memFree(byteBuffer);
        }

        Instance().m_BufferMap.put(name, buffer);
        return buffer;
    }

    /**
     * Uses the amount of channels and the sample size to decide whether we should use
     * <ul>
     *     <li><b>AL_FORMAT_MONO8/AL_FORMAT_MONO16</b></li>
     *     <li><b>AL_FORMAT_STEREO8/AL_FORMAT_STEREO16</b></li>
     * </ul>
     * @param channels the number of channels from the audio file
     * @param sampleSize the sample size of the audio file
     * @return the OpenAL flag for Mono8-16 or Stereo8-16
     */
    private static int FileFormat(int channels, int sampleSize) {
        int format;

        if(channels == 1) format = sampleSize == 8 ? AL11.AL_FORMAT_MONO8 : AL11.AL_FORMAT_MONO16;
        else format = sampleSize == 8 ? AL11.AL_FORMAT_STEREO8 : AL11.AL_FORMAT_STEREO16;

        return format;
    }
}
