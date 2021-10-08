package com.example.dave.gameEngine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.example.dave.gameEngine.dataDriven.parser.MyParseException;
import com.example.dave.gameEngine.dataDriven.parser.xml.XML_Parser;
import com.example.dave.gameEngine.myMultimedia.MyMusic;
import com.example.dave.gameEngine.myMultimedia.MySound;
import com.example.dave.gameEngine.myMultimedia.Playlist;

import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.dave.gameEngine.MyMath.readFloats_xml;

public class MainActivity extends Activity {
    public static MainActivity thisActivity=null;
    public static MainMenu mm=null;
    //XML Values
    public static String TAG;
    public static int screenSignalPixel=0;
    public static boolean allowLightSignal, checkHoldInTouch, enableOnHitByElementBehaviour,
            scrollBackground, fixKinematicPolygonChoreography,
            untangleMain, defaultMissingFlag, avoidAesthetic;
	public static int identifiedGameObjectCacheSize;
	public static long stuckTimeout;
	public static float velocityThreshold, directionChangeThreshold;

	Audio audio;
    // the tag used for logging
    public static int screenWidth, screenHeight;
    public static AccelerometerListener accelerometerListener = null;
    public SharedPreferences preferences;

	public static int readIntOnDemand(int id) {
        Resources res=thisActivity.getResources();
        return res.getInteger(id);
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /**Res constants*/
        Resources res = getResources();
        TAG = getString(R.string.app_name);
        allowLightSignal = res.getBoolean(R.bool.allowLightSignal);
        avoidAesthetic = res.getBoolean(R.bool.avoidAesthetic);
        enableOnHitByElementBehaviour = res.getBoolean((R.bool.enableOnHitByElementBehaviour));
        checkHoldInTouch = res.getBoolean(R.bool.checkTouchHold);
        scrollBackground = res.getBoolean(R.bool.scrollBackground);
        fixKinematicPolygonChoreography = res.getBoolean(R.bool.fixKinematicPolygonChoreography);
        _Log.LOG_ACTIVE = res.getBoolean(R.bool.logActive);
        if (allowLightSignal) //else is 0
            screenSignalPixel = res.getInteger(R.integer.screenSignalPixel);
        identifiedGameObjectCacheSize = res.getInteger(R.integer.identifiedGameObjectCacheSize);
        defaultMissingFlag = res.getBoolean(R.bool.defaultMissingFlag);
        stuckTimeout = res.getInteger(R.integer.stuckTimeoutMillis) * 1_000_000L;
        untangleMain = res.getBoolean(R.bool.untangleMain);
        if (untangleMain) {
            velocityThreshold = readFloats_xml(res.getInteger(R.integer.velocityThreshold), res.getInteger(R.integer.vT_floatDigits));
            directionChangeThreshold = readFloats_xml(res.getInteger(R.integer.directionChangeThreshold), res.getInteger(R.integer.dcT_floatDigits));
        }
        this.preferences = getPreferences(Context.MODE_PRIVATE);

        //Test stuff here

        // Sound
        audio = new AndroidAudio(this);
        MySound.init(audio);
        MyMusic.init(audio);

        if (_Log.LOG_ACTIVE) {
            _Log.i("Main thread", "create. Menu has " + MainMenu.count + " entries");
            _Log.i("Here we go");
        }
        //Pre Loading Data Driven
        try {
            if (_Log.LOG_ACTIVE) {
                _Log.i("Preloading AI");
            }
            XML_Parser.getInstance().parseStdAI_Engines(res.openRawResource(R.raw.ai_engine_standards));
            if (_Log.LOG_ACTIVE) {
                _Log.i("Preloading gObjects");
            }
            XML_Parser.getInstance().parseStdGOProperties(res.openRawResource(R.raw.go_properties_std));
            if (_Log.LOG_ACTIVE) {
                _Log.i("Loaded");
            }
        } catch (Exception e) {
            if (_Log.LOG_ACTIVE) {
                _Log.e("Parser", e);
            }
        }

        // Just for info
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRate = display.getRefreshRate();

        // Accelerometer
        SensorManager smanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (smanager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty()) {
            if (_Log.LOG_ACTIVE) {
                _Log.w("No accelerometer");
            }
        } else {
            Sensor accelerometer = smanager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            accelerometerListener = new AccelerometerListener();
            if (!smanager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL))
                if (_Log.LOG_ACTIVE) {
                    _Log.e("Could not register accelerometer listener");
                }
        }

        // Game world
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        //Setting up final touches
        //GraphicSubSys.setGraphicSubSys();
        GraphicSubSys.setGraphicSubSys();

        mm = MainMenu.getInstance(this);
        {
            final MainMenu.MenuEntry behavior[] = new MainMenu.MenuEntry[5];
            MainMenu.MenuEntry entry = new MainMenu.MenuEntry();
            entry.buttonID = R.id.buttonTutorial;
            entry.content_Index = mm.createLvlContent(MyMusic.lightRain, "Tutorial");
            entry.enableIf = null;
            behavior[0] = entry;
            entry = new MainMenu.MenuEntry();
            entry.buttonID = R.id.button1;
            entry.content_Index = mm.createLvlContent(MyMusic.lightRain, "Level1");
            entry.enableIf = "Tutorial_Completed";
            behavior[1] = entry;
            entry = new MainMenu.MenuEntry();
            entry.buttonID = R.id.button2;
            entry.content_Index = mm.createLvlContent(MyMusic.lightRain, "Level2");
            entry.enableIf = "Lvl_1_Completed";
            behavior[2] = entry;
            entry = new MainMenu.MenuEntry();
            entry.buttonID = R.id.button3;
            entry.content_Index = mm.createLvlContent(MyMusic.lightRain, "Level3");
            entry.enableIf = "Lvl_2_Completed";
            behavior[3] = entry;
            entry = new MainMenu.MenuEntry();
            entry.buttonID = R.id.buttonCredits;
            entry.content_Index = mm.createCreditsContent(MyMusic.lazy, "Credits");
            entry.enableIf = null;
            behavior[4] = entry;
            mm.createMenuContent(Playlist.menuMusic, R.layout.activity_main, behavior);
        }
        mm.changeContent(0);
        if(_Log.LOG_ACTIVE){
            _Log.i("onCreate complete, Endianness = " +
                (ByteOrder.nativeOrder()==ByteOrder.BIG_ENDIAN? "Big Endian" : "Little Endian"));}
    }

    @Override
    public void onPause() {
        super.onPause();
        if(_Log.LOG_ACTIVE){
            _Log.i("Main thread", "pause");
            _Log.i("Main Menu", "curr is "+mm.current);}
        mm.pause();
        MyMusic.eventualPause();

        // persistence example
        /*
            SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(getString(R.string.important_info), 0);
            editor.commit();
         */
    }

    @Override
    public void onStop() {
        super.onStop();
        mm.stop();
        if(_Log.LOG_ACTIVE){
            _Log.i("Main thread", "stop");}

    }

    @Override
    public void onResume() {
        super.onResume();
        if(_Log.LOG_ACTIVE){
            _Log.i("Main thread", "resume. Menu has "+mm.count+" entries");}

        mm.resume();
        MyMusic.eventualResume();
    }
}
