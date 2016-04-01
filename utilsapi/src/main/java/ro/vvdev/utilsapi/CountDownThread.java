package ro.vvdev.utilsapi;

/**
 * Created by victor on 14.10.2015.
 */
public abstract class CountDownThread extends Thread{

    private long duration;
    private boolean run=true;
    private boolean pause=false;
    private boolean hasFinished = false;
    private int tick = 100;

    public CountDownThread(long duration){
        this.duration=duration;
    }

    public void run(){
        long tickStart;
        long tickDuration;
        try{
            while(run){
                /*synchronized (CountDownThread.this) {
                    while(pause)
                        wait();
                }
                duration-=tick;
                if(duration<=0){
                    hasFinished = true;
                    onFinish();
                    return;
                }
                onTick(duration);
                Thread.sleep(tick);*/

                if(!pause)
                    duration-=tick;
                if(duration<=0){
                    hasFinished = true;
                    onFinish();
                    return;
                }
                tickStart = System.currentTimeMillis();
                onTick(duration);
                tickDuration = System.currentTimeMillis() - tickStart;
                //System.out.println(tickDuration);
                if(tick - tickDuration > 0)
                    Thread.sleep(tick - tickDuration);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean hasFinished(){
        return hasFinished;
    }

    public void pause(){
        pause=true;
    }

    public void unpause(){
        synchronized (CountDownThread.this) {
            pause=false;
            notify();
        }
    }

    public boolean isPaused(){
        return pause;
    }

    public abstract void onTick(long milisecondsTillFinish);
    public abstract void onFinish();
}
