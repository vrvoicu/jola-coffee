package ro.vvdev.utilsapi;

/**
 * Created by victor-vm on 7/14/2015.
 */
public class ThreadUtils {

    public static void localSleep(long millis){
        try{
            Thread.sleep(millis);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void globalWait(Object commonObject){
        if(commonObject == null)
            return;

        synchronized (commonObject){
            try {
                commonObject.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void globalNotifyAll(Object commonObject){
        if(commonObject == null)
            return;

        synchronized (commonObject){
            commonObject.notifyAll();
        }
    }

    public static void localWait(Runnable runnable){
        if(runnable == null)
            return;

        try {
            synchronized (runnable) {
                runnable.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void localNotify(Runnable runnable){
        if(runnable == null)
            return;

        synchronized (runnable){
            runnable.notifyAll();
        }
    }

}
