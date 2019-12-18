package ss.furniturear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MakeActivity extends AppCompatActivity {
    int width = 0, height = 0, length = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_make);

        Button btn = (Button) findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //키 값으로 width,height,length 저장하고 Intent이용해서 Main으로 값 넘겨줌

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("length", length);
                Toast.makeText(getApplicationContext(), width + " X " + height + " X " + length + " 저장완료", Toast.LENGTH_LONG).show();


                //IntentPage Activity에 데이터를 전달.
                startActivity(intent);  //인텐트를 시작한다.
            }
        });


        //seekBar와 textview 선언

        SeekBar seekBar_width = (SeekBar) findViewById(R.id.seekBar_width);
        TextView textView_width = (TextView) findViewById(R.id.textView_width);

        SeekBar seekBar_height = (SeekBar) findViewById(R.id.seekBar_height);
        TextView textView_height = (TextView) findViewById(R.id.textView_height);

        SeekBar seekBar_length = (SeekBar) findViewById(R.id.seekBar_length);
        TextView textView_length = (TextView) findViewById(R.id.textView_length);


        //최대 100cm까지
        seekBar_width.setMax(100);
        seekBar_height.setMax(100);
        seekBar_length.setMax(100);

        //width 단위 10씩 설정가능한 seekbar 이하 height,length 동일
        seekBar_width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress % 10 == 0) {
                    textView_width.setText(String.valueOf(progress));
                } else {
                    seekBar.setProgress((progress / 10) * 10);
                    textView_width.setText(String.valueOf((progress / 10) * 10) + "cm");
                    width = (progress / 10) * 10;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress % 10 == 0) {
                    textView_height.setText(String.valueOf(progress));
                } else {
                    seekBar.setProgress((progress / 10) * 10);
                    textView_height.setText(String.valueOf((progress / 10) * 10) + "cm");
                    height = (progress / 10) * 10;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_length.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress % 10 == 0) {
                    textView_length.setText(String.valueOf(progress));
                } else {
                    seekBar.setProgress((progress / 10) * 10);
                    textView_length.setText(String.valueOf((progress / 10) * 10) + "cm");
                    length = (progress / 10) * 10;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
