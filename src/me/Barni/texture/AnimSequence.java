package me.Barni.texture;

public class AnimSequence {

    String name, nextName;

    private int[] frames, delays;

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
        return frames[currentFrame];
    }

    public int getFrameCount() {
        return frameCount;
    }

    public boolean isEnded() {
        return currentFrame >= frameCount-1;
    }

    private int timer, currentFrame, frameCount;

    void update() {
        timer++;

        if (currentFrame >= frameCount) {
            currentFrame = 0;
            timer = 0;
        }


        if (timer >= delays[currentFrame]) {
            currentFrame++;
            timer = 0;
        }
    }

    public void reset()
    {
        timer = 0;
        currentFrame = 0;
    }

    public AnimSequence(String name, String nextName, int[] frames, int[] delays, int frameCount) {
        this.frameCount = frameCount;
        this.name = name;
        this.nextName = nextName;
        this.frames = frames;
        this.delays = delays;
    }
}
