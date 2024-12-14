package sounds;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


//TAKEN FROM SOURCE, WILL MODIFY LATER ON
public class Sound {

    //for individual wav sounds (not looped)
    //http://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
    public static synchronized void playSound(final String strPath) {
        new Thread(() -> {
            try {
                Clip clp = AudioSystem.getClip();

                AudioInputStream aisStream =
                        AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream("/sounds/" + strPath));

                clp.open(aisStream);
                clp.start();
            } catch (Exception e) {
                System.err.println("Error loading sound: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }


    //for looping wav clips
    //http://stackoverflow.com/questions/4875080/music-loop-in-java
    public static Clip clipForLoopFactory(String strPath){
        Clip clp = null;

        try {
            AudioInputStream aisStream =
                    AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream("/sounds/" + strPath));
            clp = AudioSystem.getClip();
            clp.open(aisStream);

        } catch(Exception exp){
            System.out.println("Error loading sound: " + exp.getMessage());
            exp.printStackTrace();
        }

        return clp;

    }




}
