package ca.hackercat.magma.io;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.util.MagmaMath;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {

    private static final Logger LOGGER = Logger.get(Sound.class);

    private int bufferID;
    private int sourceID;
    private String path;


    private boolean isPlaying = false;

    public Sound(String path, boolean loops) {
        this.path = path;

        // allocate space
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        InputStream is = FileUtils.getInputStream(path);

        byte[] data = null;

        try {
            data = is.readAllBytes();
        }
        catch (IOException e) {
            LOGGER.error(e);
        }

        ByteBuffer inBuffer = BufferUtils.createByteBuffer(data.length);
        inBuffer.put(data).flip();


        ShortBuffer rawAudioBuffer = stb_vorbis_decode_memory(inBuffer, channelsBuffer, sampleRateBuffer);



        if (rawAudioBuffer == null) {
            LOGGER.error("Error getting audio data for '" + path + "'");
        }

        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        stackPop();
        stackPop();

        int format = -1;

        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        }
        else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }
        else {
            LOGGER.warn("bad channel count '" + channels + "'");
        }

        bufferID = alGenBuffers();
        alBufferData(bufferID, format, rawAudioBuffer, sampleRate);

        sourceID = alGenSources();

        alSourcei(sourceID, AL_BUFFER, bufferID);
        alSourcei(sourceID, AL_LOOPING, loops ? 1 : 0);
        alSourcei(sourceID, AL_POSITION, 0);
        alSourcef(sourceID, AL_GAIN, 1f);

        free(rawAudioBuffer);

    }

    private float toDB(float linear) {
        return 10 * MagmaMath.log10(linear);
    }
    private float toLinear(float db) {
        return MagmaMath.pow(10, db / 10);
    }

    public void close() {
        alDeleteSources(sourceID);
        alDeleteBuffers(bufferID);
    }

    public void play() {
        int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
            alSourcei(sourceID, AL_POSITION, 0);
        }

        if (!isPlaying) {
            alSourcePlay(sourceID);
            isPlaying = true;
        }

    }

    public void stop() {
        if (isPlaying) {
            alSourceStop(sourceID);
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
        }
        return isPlaying;
    }
}
