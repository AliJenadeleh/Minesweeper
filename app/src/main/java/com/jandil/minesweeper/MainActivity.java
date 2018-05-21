package com.jandil.minesweeper;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ImageButton.OnClickListener ,
ImageButton.OnLongClickListener{

    TextView txtTimer,txtFlags,txtResult;

    private int seconds,minutes,hours,moveCounter;
    private int[] Bombs = new int[5];
    private Handler timer;
    private  Runnable runnable ;
    private int[][] btnArry = {
            {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6},
            {R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn10, R.id.btn11, R.id.btn12},
            {R.id.btn13, R.id.btn14, R.id.btn15, R.id.btn16, R.id.btn17, R.id.btn18},
            {R.id.btn19, R.id.btn20, R.id.btn21, R.id.btn22, R.id.btn23, R.id.btn24},
            {R.id.btn25, R.id.btn26, R.id.btn27, R.id.btn28, R.id.btn29, R.id.btn30},
            {R.id.btn31, R.id.btn32, R.id.btn33, R.id.btn34, R.id.btn35, R.id.btn36}
    };

    private void InitialRunnable(){
        if(runnable == null)
            runnable = new Runnable() {
                @Override
                public void run() {
                    seconds ++;
                    if(seconds >= 60)
                    {
                        seconds = 0;
                        minutes ++;
                    }
                    if(minutes >= 60)
                    {
                        minutes = 0;
                        hours++;
                    }
                    txtTimer.setText(hours + ":" + minutes + ":" + seconds);

                    timer.postDelayed(this,1000);
                }
            };
    }

    private void startTiming(){
        InitialRunnable();
        hours = minutes = seconds = 0;

        if(timer == null)
            timer = new Handler();
        else
            timer.removeCallbacks(runnable);
        timer.postDelayed(runnable,1000);
    }

    private void setBombs(){
        List<Integer> test = new ArrayList<Integer>();
        for(int i=0;i<36;i++)
          test.add(i);
        Random random = new Random();
        for(int i=0;i<5;i++){
           int k = random.nextInt(test.size());
            Bombs[i] = test.get(k);
            test.remove(k);
        }
    }

    private boolean hasBomb(int tag){

        for(int i=0;i<Bombs.length;i++)
         if(Bombs[i] == tag)
             return true;
        return false;
    }

    private  boolean hasBomb(int row,int col){
        if(!isInRange(row,col)) return  false;

        int tmp = btnArry[row][col];
        View v = findViewById(tmp);
        tmp = (int) v.getTag();

        return hasBomb(tmp);
    }

    private int isBombNearCounter(int row,int col)
    {
        int count = 0;
        if(hasBomb(row,col)) count++;
        if(hasBomb(row,col-1))count++;
        if(hasBomb(row,col+1))count++;
        if(hasBomb(row -1,col-1))count++;
        if(hasBomb(row-1,col))count++;
        if(hasBomb(row-1,col+1))count++;
        if(hasBomb(row+1,col-1))count++;
        if(hasBomb(row+1,col))count++;
        if(hasBomb(row+1,col+1))count++;

        return count;
    }

    private boolean isBombNear(int row,int col)
    {
        return hasBomb(row,col-1) ||
                hasBomb(row,col+1) ||
                hasBomb(row -1,col-1) ||
                hasBomb(row-1,col)||
                hasBomb(row-1,col+1)||
                hasBomb(row+1,col-1)||
                hasBomb(row+1,col)||
                hasBomb(row+1,col+1);
    }

    private void showBombs(int ignore)
    {
        int inx = 0;
        for(int i =0;i<6;i++)
            for(int j = 0;j<6;j++)
            {
                if(inx != ignore && hasBomb(inx)) {
                    int id = btnArry[i][j];
                    ImageButton btn = (ImageButton) findViewById(id);
                    btn.setImageResource(R.drawable.mine);
                }
                inx++;
            }
    }

    private void InitialBombs(){
        txtResult.setText("");
        setBombs();
        int inx = 0;
        moveCounter = 31;
        for(int i =0;i<6;i++)
            for(int j = 0;j<6;j++)
            {
                int id = btnArry[i][j];
                ImageButton btn = (ImageButton) findViewById(id);
                btn.setMaxHeight(50);
                btn.setMinimumHeight(50);
                btn.setMinimumWidth(50);
                btn.setTag(inx);
                btn.setImageResource(0);
                btn.setImageResource(R.color.btnBG);
                btn.setOnClickListener(this);
                inx++;
            }

            startTiming();
    }

       private void Bomb(View v,int tag){
        timer.removeCallbacks(runnable);
        ImageButton btn = (ImageButton) v;
        btn.setImageResource(R.drawable.bang);
        showBombs(tag);
           txtResult.setText(R.string.fail);
    }
    private int getImageId(int count){
        if(count == 1)
            return  R.drawable.b1;
        if(count == 2)
            return  R.drawable.b2;
        if(count == 3)
            return  R.drawable.b3;
        if(count == 4)
            return  R.drawable.b4;
        if(count == 5)
            return  R.drawable.b5;

        return 0;
    }
    private void someBombs(View v)
    {
        ((ImageButton) v).setImageResource(R.color.colorAccent);
    }
    private void someBombs(View v,int count)
    {
        int id = getImageId(count);
        ((ImageButton) v).setImageResource(id);
    }
    private void NoBomb(View v)
    {
        NoBomb((ImageButton) v);
    }

    private void NoBomb(ImageButton b){
        b.setTag(-1);
        b.setImageResource(R.color.btnClear);
        moveCounter --;

        txtFlags.setText("" + moveCounter);

        if(moveCounter == 0)
         Win();

    }

    private void Win(){
        timer.removeCallbacks(runnable);
        txtResult.setText(R.string.win);
    }

    private boolean isInRange(int col,int row)
    {
        return (col >= 0 && col < 6 && row >= 0 && row < 6);
    }

    private boolean isInGame(View v)
    {
        return  (int)v.getTag() != -1;
    }

    private  void trigger(int row,int col)
    {
        if(isInRange(row,col) )
        {

            int id = btnArry[row][col];
            View v = findViewById(id);
            if(isInGame(v)) {
                int tag = (int) v.getTag();
                if (!hasBomb(tag) && !isBombNear(row,col)) {
                    NoBomb(v);
                    scan(row,col);
                }
                else{
                    int count = isBombNearCounter(row,col);
                    if(count == 0)
                     someBombs(v);
                    else
                        someBombs(v,count);
                }

            }
        }
    }

    private void scan(int row,int col)
    {
        trigger(row,col-1);
        trigger(row,col+1);

        trigger(row -1,col-1);
        trigger(row -1,col);
        trigger(row -1,col+1);

        trigger(row +1,col-1);
        trigger(row +1,col);
        trigger(row +1,col+1);
    }
    private void scan(int tag){
        int col,row;
        row = tag / 6;
        col = tag % 6;

         scan(row,col);
     }

// ------------------------------------------------------
    @Override
    public void onClick(View v) {
        int tag =(int) v.getTag();
            if(tag == -1) return;

         if(hasBomb(tag))
         {
             Bomb(v,tag);
         }
         else{
             NoBomb(v);
             scan(tag);
         }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTimer = (TextView) findViewById(R.id.txtTimer);
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtFlags = (TextView) findViewById(R.id.txtFlags);
        InitialBombs();
    }

    public void btnExit_OnClick(View view) {
        System.exit(0);
    }

    public void btnRestrat_OnClick(View view) {
        InitialBombs();
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
