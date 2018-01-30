package com.tarun.currensee;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


public class NoteActivity extends AppCompatActivity {

    private static final String MODEL_FILE = "file:///android_asset/model.pb";
    private int numberOfClasses = 0;
    private Vector<String> labels = new Vector<String>();
    private MSCognitiveServicesClassifier classifier;
    Display display;
    int screenOrientation;

    CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);


        cameraView = findViewById(R.id.camera_view);
        classifier = new MSCognitiveServicesClassifier(NoteActivity.this);
        display = getWindowManager().getDefaultDisplay();
        screenOrientation = display.getOrientation();


        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                Log.e("sid", "picture taken");
                super.onPictureTaken(jpeg);
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(jpeg);
                Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);

                Classifier.Recognition r = classifier.classifyImage(bitmap, screenOrientation); //
                // should be sensor orientation

                if (r.getConfidence() > 0.91) {
                    Intent intent = new Intent(NoteActivity.this, ProcessNoteActivity.class);
                    intent.putExtra("denomination", r.getTitle());
                    startActivity(intent);
                }
                Log.e("sid", r.toString());
            }
        });


        Observable.interval(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
//                cameraView.capturePicture();
                cameraView.captureSnapshot();
            }
        });

        loadLabels(this);

    }

    private void loadLabels(final Context context) {
        final AssetManager assetManager = context.getAssets();

        // loading labels
        BufferedReader br = null;
        try {
            final InputStream inputStream = assetManager.open("labels.txt");
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line);
            }
            br.close();

            numberOfClasses = labels.size();
        } catch (IOException e) {
            throw new RuntimeException("error reading labels file!", e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }
}
