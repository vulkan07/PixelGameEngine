package me.Barni.texture;

public class AnimSequence {

    String name, nextName;

    private Texture texture;
    private int[] frames, delays;
    private boolean valid;

    public int[] getFrames() {
        return frames;
    }

    public int[] getDelays() {
        return delays;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
        this.timer = 0;
    }

    public int getCurrentFrame() {
        if (texture.isAnimated())
            checkCurrentFrameIsValid();
        return frames[currentFrame];
    }

    public int getFrameCount() {
        return frameCount;
    }

    public boolean isEnded() {
        return !valid;
    }

    private int timer, currentFrame, frameCount;

    void update() {
        if (!texture.isAnimated() || !valid)
            return;

        timer++;
        //On timer
        if (timer >= delays[currentFrame]) {
            texture.uploadImageToGPU(true, currentFrame);
            currentFrame++;
            timer = 0;
        }
        checkCurrentFrameIsValid();
    }

    private void checkCurrentFrameIsValid() {
        //On last frame
        if (currentFrame >= frameCount) {
            currentFrame = frameCount-1;
            valid = false;
        }
    }

    public void reset() {
        valid = true;
        timer = 0;
        currentFrame = 0;
    }

    public AnimSequence(Texture txt, String name, String nextName, int[] frames, int[] delays, int frameCount) {
        this.texture = txt;
        this.frameCount = frameCount;
        this.name = name;
        this.nextName = nextName;
        this.frames = frames;
        this.delays = delays;
        this.valid = true;
    }
}
