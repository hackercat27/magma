package ca.hackercat.magma.io;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.object.Updatable;
import ca.hackercat.magma.util.MagmaMath;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundEventManager extends Updatable {

    private static final String DEFAULT_PATH = "/assets/sounds.json";
    private static final Logger LOGGER = Logger.get(SoundEventManager.class);

    private final static List<SoundEventManager> instances = new ArrayList<>();

    private SoundEvent[] events;

    private List<Sound> openSounds = new ArrayList<>();
    private Map<String, byte[]> soundDataMap = new HashMap<>();

    public void createSoundEvent(String name) {
        Sound sound = getSound(name);
        if (sound == null)
            return;

        sound.play();
    }

    private static class SoundEvent {
        String event;
        String[] sounds;
        boolean stream;
    }

    public SoundEventManager() {
        this(DEFAULT_PATH);
    }
    public SoundEventManager(String path) {
        events = new Gson().fromJson(FileUtils.getContents(path), SoundEvent[].class);
        instances.add(this);
    }

    private Sound getSound(String name) {
        if (events == null) {
            LOGGER.warn("Sound events not initialized!");
            return null;
        }

        for (SoundEvent event : events) {
            if (event.event.equals(name)) {
                int index = (int) (MagmaMath.randomf() * event.sounds.length);
                String path = event.sounds[index];

                byte[] data;

                if (soundDataMap.containsKey(path)) {
                    data = soundDataMap.get(path);
                }
                else {
                    InputStream is = FileUtils.getInputStream(path);

                    try {
                        data = is.readAllBytes();
                    }
                    catch (IOException e) {
                        LOGGER.error(e);
                        return null;
                    }
                    soundDataMap.put(path, data);
                }

                Sound sound = new Sound(data, false);
                openSounds.add(sound);
                return sound;
            }
        }
        return null;
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void close() {

    }

    public static void cleanManagers() {
        for (SoundEventManager instance : instances) {
            List<Sound> soundsToRemove = new ArrayList<>();
            for (Sound sound : instance.openSounds) {
                if (!sound.isPlaying()) {
                    sound.close();
                    soundsToRemove.add(sound);
                }
            }
            instance.openSounds.removeAll(soundsToRemove);
        }
    }
}
