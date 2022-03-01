package com.example.mainactivity.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CameraProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.FragmentCameraBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import render.animations.Attention;
import render.animations.Render;

import static org.webrtc.ContextUtils.getApplicationContext;

public class CameraFrag extends Fragment {
    public CameraFrag() {
    }

    FragmentCameraBinding binding;
    private int REQUEST_CODE_PERMISSIONS = 101;
    private String[] REQUIRED_PERMISSION = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        if (allPermissionsGranted()) {
            startCamera();
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSION, REQUEST_CODE_PERMISSIONS);
        }
        return binding.getRoot();
    }

    private void startCamera() {
        CameraX.unbindAll();
        Rational aspectratio = new Rational(binding.texture.getWidth(), binding.texture.getHeight());
        Size screen = new Size(binding.texture.getWidth(), binding.texture.getHeight());
        PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectratio).setTargetResolution(screen).build();
        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = ((ViewGroup) binding.texture.getParent());
                parent.removeView(binding.texture);
                parent.addView(binding.texture);
                binding.texture.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });
        ImageCaptureConfig imageCaptureConfig=new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imageCapture= new ImageCapture(imageCaptureConfig);

        binding.imageView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Render render=new Render(getActivity());
                render.setAnimation(Attention.Pulse(binding.imageView11));
                render.setDuration(500);
                render.start();
                File filepath=Environment.getExternalStorageDirectory();
                File dir =new File(filepath.getAbsolutePath()+"VibeX");
                dir.mkdir();
                String filename=String.format("%d.jpg",System.currentTimeMillis());
                Toast.makeText(getActivity(),filename, Toast.LENGTH_SHORT).show();
                File file=new File(dir,filename);
                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                      //  Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        File filepath=Environment.getExternalStorageDirectory();
                        File dir =new File(filepath.getAbsolutePath()+"VibeX");
                        dir.mkdir();
                        String filename=String.format("%d.jpg",System.currentTimeMillis());
                        Toast.makeText(getActivity(),filename, Toast.LENGTH_SHORT).show();
                        File file=new File(dir,filename);
                    }
                });
            }
        });
        CameraX.bindToLifecycle(getActivity(),preview,imageCapture);
    }

    private void updateTransform() {

        Matrix mx = new Matrix();
        float w = binding.texture.getMeasuredWidth();
        float h = binding.texture.getMeasuredHeight();

        float cx = w / 2f;
        float cy = h / 2f;
        int RotationGDgr;
        int rotation = (int) binding.texture.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                RotationGDgr = 0;
                break;
            case Surface.ROTATION_90:
                RotationGDgr = 90;
                break;
            case Surface.ROTATION_180:
                RotationGDgr = 180;
                break;
            case Surface.ROTATION_270:
                RotationGDgr = 270;
                break;
            default:
                return;
        }
        mx.postRotate((float) RotationGDgr, cx, cy);
        binding.texture.setTransform(mx);
    }

    private boolean allPermissionsGranted() {
        for (String permissions : REQUIRED_PERMISSION) {
            if (ContextCompat.checkSelfPermission(getActivity(), permissions) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}