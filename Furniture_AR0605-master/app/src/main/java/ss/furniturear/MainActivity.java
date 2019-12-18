package ss.furniturear;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Toast;

import com.google.ar.core.Anchor;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.math.Vector3;

import java.io.File;
import java.io.FileOutputStream;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String CAPTURE_PATH = "/CAPTURE_TEST";
    // ARCore 사용에 필요한 OpenGL ES 최소버전

    private static final double MIN_OPENGL_VER = 3.0;

    private ArFragment arFragment;
    // 가구를 선택하기 위한 FloatingActionButton
    private FloatingActionButton btnSelect, btnChair, btnCouch, btnTable, btnMake, btnCap, btnDel, btnSet;

    // 현재 선택된 가구
    private int currentModel = -1;
    // ModelRenderable을 불러놓을 배열을 정의
    private ModelRenderable[] models;

    // FloatingActionButton을 열고 닫는 애니메이션
    private Animation fabOpen, fabClose;
    // FloatingActionButton이 열려 있는지 확인하기 위한 변수
    private boolean isButtonsOpened = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //intent로 가로세로높이 값 받아온것
        Intent intent = new Intent(this.getIntent());
        float width = (float) intent.getIntExtra("width", 0);
        float height = (float) intent.getIntExtra("height", 0);
        float length = (float) intent.getIntExtra("length", 0);


        // 장치가 OpenGL ES 3.0을 지원하지 않으면 종료
        if (!checkCompatibility(this)) {
            return;
        }

        setContentView(R.layout.activity_main);

        models = new ModelRenderable[6];

        // View 초기화
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        btnSelect = findViewById(R.id.button_select);
        btnChair = findViewById(R.id.button_chair);
        btnCouch = findViewById(R.id.button_couch);
        btnTable = findViewById(R.id.button_table);
        btnMake = findViewById(R.id.button_make);
        btnCap = findViewById(R.id.button_cap);
        btnDel = findViewById(R.id.button_del);
        btnSet = findViewById(R.id.button_set);

        // Animation 초기화
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        // Model 추가
        addModel(0, R.raw.chair);
        addModel(1, R.raw.couch);
        addModel(2, R.raw.table);


        // ArFragment를 터치했을 때의 event listener
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            // currentModel은 가구가 선택되지 않았을 때 -1이므로 선택을 유도
            if (currentModel < 0) {
                Toast.makeText(this, "가구를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            //원하는 크기 블럭 생성 cube 형태
            if (currentModel == 3) {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());


                MaterialFactory.makeTransparentWithColor(getApplicationContext(), new Color(244, 244, 244))
                        .thenAccept(
                                material -> {

                                    Vector3 vector3 = new Vector3((float) width / 100, (float) height / 100, (float) length / 100);
                                    ModelRenderable model = ShapeFactory.makeCube(vector3,
                                            Vector3.zero(), material);
                                    model.setShadowCaster(false);
                                    model.setShadowReceiver(false);

                                    TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                                    transformableNode.setParent(anchorNode);
                                    transformableNode.setRenderable(model);
                                    transformableNode.select();
                                });
            }


            //Toast.makeText(this, "" + currentModel, Toast.LENGTH_SHORT).show();
            // Anchor, AnchorNode 초기화
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            // 추가할 node 초기화 및 추가
            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
            transformableNode.setParent(anchorNode);
            //선택되어있는 model을 node로 설정
            transformableNode.setRenderable(models[currentModel]);
            transformableNode.select();
        });

        // FloatingActionButton들을 보이고 숨기기 위한 버튼의 click listener
        btnSelect.setOnClickListener(view -> {
            // 열려있으면 닫고 닫혀있으면 엶
            if (isButtonsOpened) {
                openButtons(btnChair, btnCouch, btnTable, btnMake, btnCap, btnDel, btnSet);
                btnSelect.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            } else {
                closeButtons(btnChair, btnCouch, btnTable, btnMake, btnCap, btnDel, btnSet);
                btnSelect.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            }

            // 앞에서 열려있으면 닫고 닫혀있으면 열었으므로 switch 변수도 reverse
            isButtonsOpened = !isButtonsOpened;
        });

        // 각 FloatingActionButton을 클릭했을 때 화면에 추가할 가구의 index를 바꿈
        btnChair.setOnClickListener(view -> currentModel = 0);
        btnCouch.setOnClickListener(view -> currentModel = 1);
        btnTable.setOnClickListener(view -> currentModel = 2);
        btnMake.setOnClickListener(view -> {
            startActivity(new Intent(this, MakeActivity.class));
        });

        btnCap.setOnClickListener(view -> {
            takeScreenshot();
        });
        btnSet.setOnClickListener(view -> {
            currentModel = 3;
            Toast.makeText(this, "현재 저장된 크기" + width + "cm x " + height + "cm x " + length + "cm", Toast.LENGTH_SHORT).show();
        });
        //삭제
        btnDel.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));

        });

    }


    // 장치가 OpenGL ES 3.0 이상을 지원하는지 확인하기 위한 method
    private boolean checkCompatibility(final Activity activity) {
        String openGlVersion = ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                .getDeviceConfigurationInfo()
                .getGlEsVersion();

        if (Double.parseDouble(openGlVersion) < MIN_OPENGL_VER) {
            // OpenGL ES 버전이 최소 요구 조건보다 작을 경우 toast를 띄우고 종료
            Toast.makeText(activity, "OpenGL ES 3.0 이상이 필요합니다.", Toast.LENGTH_SHORT).show();
            activity.finish();

            return false;
        }

        return true;
    }

    // raw resource의 ID를 이용하여 model을 추가
    private void addModel(int index, int modelId) {
        // models의 마지막 원소로 null을 추가

        ModelRenderable.builder()
                // ID 설정
                .setSource(this, modelId)
                .build()
                // models의 마지막 원소를 설정, 마지막 원소는 null로 추가되어있음
                .thenAccept(renderable -> models[index] = renderable)
                // model 추가에 실패했을 때
                .exceptionally(throwable -> {
                    Toast toast = Toast.makeText(this, "모델을 불러오는데 실패했습니다. ID:" + modelId, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });
    }

    // FloatingActionButton들을 여는 method. 가변인자를 받아 매개변수로 들어온 모든 button을 보임
    private void openButtons(FloatingActionButton... buttons) {
        for (FloatingActionButton button : buttons) {
            button.show();
            button.startAnimation(fabOpen);
            button.setClickable(true);
        }
    }

    // FloatingActionButton들을 숨기는 method. 가변인자를 받아 매개변수로 들어온 모든 button을 숨김
    private void closeButtons(FloatingActionButton... buttons) {
        for (FloatingActionButton button : buttons) {
            button.startAnimation(fabClose);
            button.setClickable(false);
            button.hide();
        }
    }


    //화면 캡쳐하기
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {

            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


}
