package n12x3.app_ghepanh;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";
    static{
        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV Succesfully loaded!");
        }
        else{
            Log.d(TAG, "OpenCV not loaded");
        }
    }
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    RelativeLayout background;
    LinearLayout sample;
    ImageView imgvCamera, imgvGallery,imgvSave,imgvExit, imgvImage,imgvSample1,imgvSample2,imgvSample3,imgvSample4,
            imgvSample5;
    public Bitmap bmScr, imageBitmap;
    Mat imgMat, imgMatGray;

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private Uri imageUri;
    private static final int CAM_REQUEST = 1313;
    private static final int PICK_REQUEST = 1212;
//    private static final int PICK_REQUEST = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //anh xa
        background = (RelativeLayout)findViewById(R.id.backGround);
        sample = (LinearLayout)findViewById(R.id.Sample);
        imgvCamera = (ImageView)findViewById(R.id.imageViewCamera);
        imgvGallery = (ImageView)findViewById(R.id.imageViewGallery);
        imgvImage = (ImageView)findViewById(R.id.imageViewImage);
        imgvSave = (ImageView)findViewById(R.id.imageViewSave);
        imgvExit = (ImageView)findViewById(R.id.imageViewExit);
        imgvSample1 = (ImageView)findViewById(R.id.imageSample1);
        imgvSample2 = (ImageView)findViewById(R.id.imageSample2);
        imgvSample3 = (ImageView)findViewById(R.id.imageSample3);
        imgvSample4 = (ImageView)findViewById(R.id.imageSample4);
        imgvSample5 = (ImageView)findViewById(R.id.imageSample5);
        background.setDrawingCacheBackgroundColor(Color.WHITE);

        //trang thai luc bat dau cua cac chuc nang
        imgvSample1.setEnabled(false);
        imgvSample2.setEnabled(false);
        imgvSample3.setEnabled(false);
        imgvSave.setEnabled(false);
        imgvImage.setEnabled(false);

        //background.setBackgroundResource(R.drawable.anhnen);
        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
        //cac su kien

        //su kien chup anh
        imgvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAM_REQUEST);
            }
        });

        //su kien chon anh tu bo nho
        imgvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
                imgvSample1.setEnabled(true);
                imgvSample2.setEnabled(true);
                imgvSample3.setEnabled(true);
            }
        });

        //su kien chon nhan dan mau 1
        imgvSample1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ghep(1);
                imgvSave.setEnabled(true);
                imgvImage.setEnabled(true);
            }
        });

        //su kien chon nhan dan mau 2
        imgvSample2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ghep(2);
                imgvSave.setEnabled(true);
                imgvImage.setEnabled(true);
            }
        });

        //su kien chon nhan dan mau 3
        imgvSample3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dial = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Thông báo")
                        .setMessage("Xin lỗi những stiker mới vẫn đang trong thời gian phát triển.\n"
                        +"Chúng tôi sẽ cố gắng hoàn thành sớm nhất, vui lòng trở lại sau.\nChân thành cảm ơn!")
                        .setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setCancelable(false)
                        .create();
                dial.show();
            }
        });

        //su kien luu anh
        imgvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Luu();
            }
        });
        imgvImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog dial2 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Thông báo")
                        .setMessage("Bạn có muốn hoàn tác các thay đổi vừa thực hiện?")
                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imgvImage.setImageBitmap(bmScr);
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create();
                dial2.show();
                imgvImage.setEnabled(false);
                return true;
            }
        });

        //su kien thoat ung dung
        imgvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dial3 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Thông báo")
                        .setMessage("Bạn có thực sự muốn thoát?")
                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            appExit();
                            }
                        }).setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create();
                dial3.show();
            }
        });
    }
    //phuong thuc chon anh
    private void pickImage(){
        Intent intent_pick = new Intent(Intent.ACTION_PICK);
        File fileInput = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String stringFile = fileInput.getPath();
        Uri data = Uri.parse(stringFile);
        intent_pick.setDataAndType(data,"image/*");
        startActivityForResult(intent_pick,PICK_REQUEST);
    }
    //phuong thuc ghep anh
    public void Ghep(int n){
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

        } catch (IOException e) {e.printStackTrace();}
        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        imgMat = new Mat();
        Utils.bitmapToMat(bmScr,imgMat);
        imgMatGray = new Mat();
        Imgproc.cvtColor(imgMat,imgMatGray,Imgproc.COLOR_RGB2GRAY); //chuyen anh mau sang anh muc xam
        MatOfRect faces = new MatOfRect();
        //nhan dien khuon mat
        mJavaDetector.detectMultiScale(imgMatGray,faces,1.1,2,10,new Size(100,100),new Size());
        Rect[] facesArray = faces.toArray();
//        for (int i = 0; i < facesArray.length; i++)
//            Imgproc.rectangle(imgMat,facesArray[i].tl(),facesArray[i].br(),new Scalar(0,255,0,255),3);
//        Utils.matToBitmap(imgMat,bmScr);
//        imgvImage.setImageBitmap(bmScr);

        //xu ly, tinh toan toa do va ghep nhan dan
       if (facesArray.length > 0){
           //khoi tao cac bitmap cua tung nhan dan
           Bitmap mong = BitmapFactory.decodeResource(getResources(),R.drawable.mong);
           Bitmap moChick = BitmapFactory.decodeResource(getResources(),R.drawable.mochick);
           Bitmap moRooster = BitmapFactory.decodeResource(getResources(),R.drawable.morooster);
           Bitmap tich = BitmapFactory.decodeResource(getResources(),R.drawable.tich);
           Bitmap non = BitmapFactory.decodeResource(getResources(),R.drawable.non);
           //tao bitmap trong co kich thuoc bang voi kich thuong cua anh goc
           imageBitmap = Bitmap.createBitmap(bmScr.getWidth(),bmScr.getHeight(), Bitmap.Config.ARGB_8888);
           for (int i = 0; i < facesArray.length; i++){
               float toadoxmo, toadoymo, toadoxmong, toadoymong, toadoxtich, toadoytich;
               Bitmap mongScale, moChickScale, moRoosterScale, tichScale, nonScale;
               mongScale = Bitmap.createScaledBitmap(mong,2*facesArray[i].width,2*facesArray[i].height,true);
               moChickScale = Bitmap.createScaledBitmap(moChick,2*facesArray[i].width,2*facesArray[i].height,true);
               moRoosterScale = Bitmap.createScaledBitmap(moRooster,2*facesArray[i].width,2*facesArray[i].height,true);
               tichScale = Bitmap.createScaledBitmap(tich,2*facesArray[i].width,2*facesArray[i].height,true);
               nonScale = Bitmap.createScaledBitmap(non,2*facesArray[i].width,2*facesArray[i].height,true);

               //truyen bitmap trong cho doi tuong canvas
               Canvas canvas=new Canvas(imageBitmap);
               //ve anh goc len doi tuong canvas cua Bitmap trong
               if (i < 1) canvas.drawBitmap(bmScr,0,0,null);
               if (n == 1){
                   toadoxmong = (float) (facesArray[i].x + facesArray[i].width/2-mongScale.getWidth()/4.6);
                   toadoymong = (float)(facesArray[i].y-mong.getWidth()/3.83);
                   toadoxmo = (float)(facesArray[i].x + facesArray[i].width/2-moChickScale.getWidth()/19.9);
                   toadoymo = (float)(facesArray[i].y+facesArray[i].height-2*moChickScale.getHeight()/9.95);
                   toadoxtich = (float)(facesArray[i].x+ facesArray[i].width/2-tichScale.getWidth()/10);
//                   toadoytich = (float)(facesArray[i].y + facesArray[i].height-tich.getHeight()/13.34);
                   toadoytich = (float)(toadoymo + mong.getHeight()/3.33/2);
                   //ghep cac nhan dan vao Bitmap da duoc ve anh goc len
                   canvas.drawBitmap(mongScale,toadoxmong,toadoymong,null);
                   canvas.drawBitmap(tichScale,toadoxtich,toadoytich,null);
                   canvas.drawBitmap(moRoosterScale,toadoxmo,toadoymo,null);
               }
               else if (n == 2){
                    toadoxmong = (float) (facesArray[i].x + facesArray[i].width/2-nonScale.getWidth()/4.4);
                    toadoymong = (float)(facesArray[i].y-nonScale.getWidth()/4);
                    toadoxmo = (float)(facesArray[i].x + facesArray[i].width/2-moChickScale.getWidth()/9.95);
                    toadoymo = (float)(facesArray[i].y+facesArray[i].height-2*moChickScale.getHeight()/9.95);
                   //ghep cac nhan dan vao Bitmap da duoc ve anh goc len
                    canvas.drawBitmap(moChickScale,toadoxmo,toadoymo,null);
                    canvas.drawBitmap(nonScale,toadoxmong,toadoymong,null);
               }
               imgvImage.setImageBitmap(imageBitmap);
//                Toast.makeText(MainActivity.this,"toa do mong: (" + Float.toString(toadoxmong) + ";" + Float.toString(toadoymong)
//                +") \n Toa do mo: (" + Float.toString(toadoxmo) + ";"+ Float.toString(toadoymo) +")",Toast.LENGTH_LONG).show();
           }
        }else{
           AlertDialog dial4 = new AlertDialog.Builder(MainActivity.this)
                   .setTitle("Thông báo")
                   .setMessage("Ảnh không thể nhận dạng, vui lòng chọn ảnh khác")
                   .setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   }).setCancelable(false)
                   .create();
           dial4.show();
       }
    }
    //Phuong thuc luu anh
    public void Luu(){
        FileOutputStream fileOutputStream = null;
        File file = getDisc();
        if (!file.exists() && file.mkdirs()){
            Toast.makeText(this,"Can't create directory to save image",Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String date = simpleDateFormat.format(new Date());
        String name ="Img"+date+".jpg";
        String file_name =file.getAbsolutePath()+"/"+name;
        File new_file = new File(file_name);
        try {
            fileOutputStream = new FileOutputStream(new_file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            Toast.makeText(this, "Ảnh đã được lưu với tên là " +name, Toast.LENGTH_LONG).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(new_file);
    }
    //lam moi thu vien anh
    public  void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }
    //lay duongdan
    private  File getDisc(){
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file,"Image");
    }
    //thoat ung dung
    public void appExit(){
        finish();
        System.exit(1);
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAM_REQUEST){

        }
        else if (resultCode == RESULT_OK && requestCode == PICK_REQUEST){
            imageUri = data.getData();
            try{
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bmScr = BitmapFactory.decodeStream(inputStream);
            }catch (FileNotFoundException e){}
            imgvImage.setImageBitmap(bmScr);
//            Ghep(2);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
