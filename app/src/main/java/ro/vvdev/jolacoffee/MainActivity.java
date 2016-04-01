package ro.vvdev.jolacoffee;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import ro.vvdev.utilsapi.SSBRUtils;
import ro.vvdev.utilsapi.ThreadUtils;

public class MainActivity extends AppCompatActivity {

    private MyThread loadingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadingThread = new MyThread();

        loadingThread.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(loadingThread.isAlive())
            loadingThread.stopThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyThread extends Thread{

        private boolean execute = true;

        public void run(){
            ThreadUtils.localSleep(3000);

            if(execute)
                SSBRUtils.startActivity(MainActivity.this, ControlActivity.class, null);
        }

        public void stopThread(){
            execute = false;
        }

    }
}