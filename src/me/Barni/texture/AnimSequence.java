package me.Barni.texture;

//-----------------------------------
//Purpose:  Handles a sequence: updates the data.
//          The Texture, that owns this sequence, updates this on every tick.
//
//          The current "frame" is an index.
//          Every "frame" has its own values at delays[frame] and frames[frame].
//
//          This has 2 arrays: delays, frames.
//          int[] delays: Every delay (in ticks) for a given frame.
//          int[] frames: The frame index, at every index of the delays.
//
//          The sequence is considered "ended", when currentFrame == frameCount.
//          Then the 'valid' variable is set to false, as an indicator for the Texture.
//
//          "static" means the sequence consists of only one frame.
//-----------------------------------

public class AnimSequence {

    String name, nextName;

    private final Texture texture;
    private final int[] frameIndexes;
    private final int[] delays;
    private final int staticFrame; //Set to -1 if the sequence is not static.
    private boolean valid; //Gets false if the sequence is ended.

    private int timer; //Used for stepping to the current frame
    private int currentFrame; //The current frame
    private int frameCount; //Total number of frames

    //returns the array of frame indexes
    public int[] getFrameIndexes() {
        return frameIndexes;
    }

    //returns the array of delays
    public int[] getDelays() {
        return delays;
    }

    //sets the current frame
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
        this.timer = 0;
    }

    //returns the current frame
    public int getCurrentFrameIndex() {
        if (staticFrame != -1)
            return staticFrame;
        return frameIndexes[currentFrame];
    }

    //returns the number of frames
    public int getFrameCount() {
        return frameCount;
    }

    //returns true if the sequence is ended
    public boolean isEnded() {
        return !valid;
    }

    //returns true if the sequence is static
    public boolean isStatic() {
        return staticFrame != -1;
    }

    //Called by Texture (hopefully) at every frame
    void update() {
        //Return if Texture isn't animated, or the seq. is over, or is static
        if (!texture.isAnimated() || !valid)
            return;
        if (staticFrame != -1) {
            texture.uploadImageToGPU(staticFrame);
            return;
        }

        //Increment timer
        timer++;

        //If timer reaches the current frame's delay
        if (timer >= delays[currentFrame]) {
            texture.uploadImageToGPU(frameIndexes[currentFrame]); //Upload the Texture's new frame
            currentFrame++; //Set the current frame
            timer = 0; //Reset timer
        }
        checkCurrentFrameIsValid();
    }

    //If the current frame is reached the number of frames, set valid to false
    private void checkCurrentFrameIsValid() {
        //On the last frame
        if (currentFrame >= frameCount) {
            //Sets back the currentFrame to the last frame, so the Texture can still use it
            currentFrame = frameCount-1;
            valid = false;
        }
    }

    //Restarts the sequence, even if it's ended
    public void reset() {
        valid = true;
        timer = 0;
        currentFrame = 0;
    }

    //Normal Constructor
    public AnimSequence(Texture txt, String name, String nextName, int[] frames, int[] delays, int frameCount) {
        this.texture = txt;
        this.name = name;
        this.nextName = nextName;
        this.valid = true;

        this.frameCount = frameCount;
        this.frameIndexes = frames;
        this.delays = delays;

        this.staticFrame = -1;
    }

    //Static operation constructor
    //If the sequence only has one frame
    public AnimSequence(Texture txt, String name, int staticFrame) {
        this.texture = txt;
        this.name = name;
        this.valid = true;

        if (staticFrame < 0)
            throw new IndexOutOfBoundsException("Sequence static frame index out of bounds: " + staticFrame + "! Name: " + name);
        this.staticFrame = staticFrame;

        this.nextName = null;
        this.frameIndexes = null;
        this.delays = null;
    }
}
