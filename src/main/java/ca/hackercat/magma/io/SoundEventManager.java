package ca.hackercat.magma.io;

import ca.hackercat.magma.util.MagmaMath;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static ca.hackercat.logging.Logger.LOGGER;

public class SoundEventManager {

    private static final String DEFAULT_PATH = "/assets/sounds.json";

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
    }

    private SoundEvent[] events;

    private List<Sound> loadedSounds = new ArrayList<>(0);

    public void createSoundEvent(String name) {
        Sound sound = getSound(name);
        if (sound == null)
            return;

        sound.play();
    }

    private Sound getSound(String name) {
        if (events == null) {
            LOGGER.warn("Sound events not initialized!");
            return null;
        }

        for (SoundEvent event : events) {
            if (event.event.equals(name)) {
                int index = (int) (MagmaMath.randomf() * event.sounds.length);
                return new Sound(event.sounds[index], false);
            }
        }
        return null;
    }
}
