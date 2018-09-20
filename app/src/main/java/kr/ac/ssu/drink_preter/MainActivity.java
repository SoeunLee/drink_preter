package kr.ac.ssu.drink_preter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 100;
    String TAG = "CAMERA";
    private Context mContext = this;
    public Camera mCamera;
    private CameraPreview mPreview;

    String serverUri;
    UploadService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mContext = this;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Change base URL to your upload server URL.
        //edited
        //serverUri = "http://203.253.25.54/upload.php";
        serverUri = "http://203.253.25.54/upload.php/";
        service = new Retrofit.Builder().baseUrl(serverUri).client(client).build().create(UploadService.class);

        // 카메라 인스턴스 생성
        if (checkCameraHardware(mContext)) {
            mCamera = getCameraInstance();
            // 프리뷰창을 생성하고 액티비티의 레이아웃으로 지정합니다
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            Button captureButton = (Button) findViewById(R.id.button_capture);
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // JPEG 콜백 메소드로 이미지를 가져옵니다
                    mCamera.takePicture(null, null, mPicture);

                }
            });
        } else {
            Toast.makeText(mContext, "no camera on this device!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Log.e("yuri", "3");

            /* yuri edited
            android.net.Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            android.database.Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor == null)
                return;

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            */
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Toast.makeText(mContext, "Error saving!!", Toast.LENGTH_SHORT).show();
                return;
            }


            try{
//save yuriyuriyuri!!!!!!
                //Log.e("yuri", "1");
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //Log.e("yuri", "2");
                Uri uri = Uri.parse(pictureFile.getAbsolutePath());
                //Log.e("yuri", "3");
                intent.setData(uri);
                //Log.e("yuri", "4");
                sendBroadcast(intent);
                //Log.e("yuri", "5");
                //FileOutputStream os = new FileOutputStream(pictureFile);
                //os.flush();
                //os.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            //File file = new File(filePath); yuri edited

            /* check for making file yuriyuri
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), pictureFile); //yuri edited file -> pictureFile
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", pictureFile.getName(), reqFile); //yuri edited file -> pictureFile
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

//            Log.d("THIS", data.getData().getPath());

            retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) { }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
           here!!!!!!!!!!!!!! */
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // JPEG 이미지가 byte[] 형태로 들어옵니다

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Toast.makeText(mContext, "Error saving!!", Toast.LENGTH_SHORT).show();
                return;
            }


            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                //여기서 이미지 보내야해!!
                Intent intent = new Intent();
                //intent.setType("image/*");
                intent.setType("image/jpg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);

                // if(pictureFile.exists()) Log.i("File", "저장됐습니다.");
                // Toast.makeText(mContext, "무엇입니다.", Toast.LENGTH_SHORT).show();
                Thread.sleep(500);
                mCamera.startPreview();


            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (Exception e){
                Log.d(TAG, "exception: " + e.getMessage());
            }
        }
    };

    //카메라 하드웨어 지원 여부 확인

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 카메라가 최소한 한개 있는 경우 처리
            Log.i(TAG, "Number of available camera : " + Camera.getNumberOfCameras());
            return true;
        } else {
            // 카메라가 전혀 없는 경우
            Toast.makeText(mContext, "No camera found!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //카메라 인스턴스를 안전하게 획득합니다

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // 사용중이거나 사용 불가능 한 경우
        }
        return c;
    }


    // 이미지를 저장할 파일 객체를 생성합니다

    private static File getOutputMediaFile() {
        // SD카드가 마운트 되어있는지 먼저 확인해야합니다
        // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Aeye");
        // 굳이 이 경로로 하지 않아도 되지만 가장 안전한 경로이므로 추천함.

        // 없는 경로라면 따로 생성한다.
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCamera", "failed to create directory");
                return null;
            }
        }

        // 파일명을 적당히 생성. 여기선 시간으로 파일명 중복을 피한다.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        Log.i("MyCamera", "Saved at" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());

        return mediaFile;
    }

    @Override
    public void onPause() {
        super.onPause();
        // 보통 안쓰는 객체는 onDestroy에서 해제 되지만 카메라는 확실히 제거해주는게 안전하다.

    }
}
