package alreyesh.android.scanmarketclient.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.camarascan.DetectorActivity;
import alreyesh.android.scanmarketclient.customview.MultiBoxTracker;
import alreyesh.android.scanmarketclient.customview.OverlayView;
import alreyesh.android.scanmarketclient.tflite.Classifier;
import alreyesh.android.scanmarketclient.tflite.YoloV4Classifier;
import alreyesh.android.scanmarketclient.utils.ImageUtils;
import alreyesh.android.scanmarketclient.utils.Logger;
import alreyesh.android.scanmarketclient.utils.Utils;


public class HomeFragment extends Fragment {
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    // camaratensor
  /*  private static final Logger LOGGER = new Logger();

    public static final int TF_OD_API_INPUT_SIZE = 416;

    private static final boolean TF_OD_API_IS_QUANTIZED = false;

    private static final String TF_OD_API_MODEL_FILE = "scanmarket-416-fp16.tflite";

    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/clases.txt";

    // Minimum detection confidence to track a detection.
    private static final boolean MAINTAIN_ASPECT = false;
    private Integer sensorOrientation = 90;

    private Classifier detector;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;
    private OverlayView trackingOverlay;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Bitmap sourceBitmap;
    private Bitmap cropBitmap;

    private Button cameraButton, detectButton;
    private ImageView imageView;
*/

    public HomeFragment() {
        // Required empty public constructor
    }

    private void handleResult(Bitmap bitmap, List<Classifier.Recognition> results) {
        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
                canvas.drawRect(location, paint);
//                cropToFrameTransform.mapRect(location);
//
//                result.setLocation(location);
//                mappedRecognitions.add(result);
            }
        }
//        tracker.trackResults(mappedRecognitions, new Random().nextInt());
//        trackingOverlay.postInvalidate();
    //    imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().invalidateOptionsMenu();


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {





        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button  btnCamera =(Button)view.findViewById(R.id.btnScan);
       btnCamera.setOnClickListener(v -> {
           startActivity(new Intent(getActivity(), DetectorActivity.class));
        });





        getActivity().invalidateOptionsMenu();
        return view;
    }




}