package ca.hackercat.magma.io;

import ca.hackercat.logging.Logger;
import ca.hackercat.magma.object.Updatable;
import ca.hackercat.magma.util.MagmaMath;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SoundEventManager extends Updatable {

    private static final String DEFAULT_PATH = "/assets/sounds.json";
    private static final Logger LOG = Logger.get(SoundEventManager.class);

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
            LOG.warn("Sound events not initialized!");
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

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void close() {

    }
}
