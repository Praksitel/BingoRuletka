package ru.bb.BingoRuletka;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends Activity implements View.OnTouchListener {
    final int intR = 195;
    final int medR = 253;
    final int outR = 333;

    final int halfAngle = 9;

    final int lowSpeed = 15;
    final int mediumSpeed = lowSpeed*2;
    final int fastSpeed = lowSpeed*3;

    ImageView ivBig;
    ImageView ivSmall;
    ImageView ivFix;
    ImageView workIV;
    ImageView prevIV;

    long moveSmallBeginTime;
    float moveSmallBeginX;
    float moveSmallBeginY;
    float newSmallLastAngle;
    float smallLastAngle;
    double endSmallMoveSpeed = 0;
    double smallMoveSpeed = 0;

    long moveBigBeginTime;
    float moveBigBeginX;
    float moveBigBeginY;

    double smallAVOTA = 0;

    float x;
    float y;
    //float moveX = 0;
    //float moveY = 0;

    //long moveEndTime;
    //long time;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ivBig = (ImageView) findViewById(R.id.ivBig);
        ivSmall = (ImageView) findViewById(R.id.ivSmall);
        ivFix = (ImageView) findViewById(R.id.ivFix);
        workIV = null;
        prevIV = null;

        // регистрируем контекстное меню для компонента tv
        registerForContextMenu(ivBig);

        ivBig.setOnTouchListener(this);
        ivSmall.setOnTouchListener(this);
    }

    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ivBig:
                // добавляем пункты
                menu.add(0, MENU_ALPHA_ID, 0, "alpha");
                menu.add(0, MENU_SCALE_ID, 0, "scale");
                menu.add(0, MENU_TRANSLATE_ID, 0, "translate");
                menu.add(0, MENU_ROTATE_ID, 0, "rotate");
                menu.add(0, MENU_COMBO_ID, 0, "combo");
                break;
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }*/
    /*
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Animation anim = null;
        // определяем какой пункт был нажат
        switch (item.getItemId()) {
            case MENU_ALPHA_ID:
                // создаем объект анимации из файла anim/myalpha
                anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
                break;
            case MENU_SCALE_ID:
                anim = AnimationUtils.loadAnimation(this, R.anim.myscale);
                break;
            case MENU_TRANSLATE_ID:
                anim = AnimationUtils.loadAnimation(this, R.anim.mytrans);
                break;
            case MENU_ROTATE_ID:
                anim = AnimationUtils.loadAnimation(this, R.anim.myrotate);
                break;
            case MENU_COMBO_ID:
                anim = AnimationUtils.loadAnimation(this, R.anim.mycombo);
                break;
        }
        // запускаем анимацию для компонента tv
        ivBig.startAnimation(anim);
        return super.onContextItemSelected(item);
    }*/

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        long currentMoveTime;
        long time;
        float angle;
        int loa;
        int loai;
        int resangle;
        double trip;

        x = event.getX();
        y = event.getY();
        float normX = x - 333;
        float normY = y - 333;

        if (Math.hypot(normX, normY) <= intR) {
            //Log.e("BRul", "intR" + "x:"+x+"\ty:"+y + "normX:"+normX+"\tnormY:"+normY);
            workIV = ivFix;
        } else if (Math.hypot(normX, normY) <= medR) {
            //Log.e("BRul", "medR" + "x:"+x+"\ty:"+y + "normX:"+normX+"\tnormY:"+normY);
            workIV = ivSmall;
        } else if (Math.hypot(normX, normY) <= outR) {
            Log.e("BRul", "outR" + "x:"+x+"\ty:"+y + "normX:"+normX+"\tnormY:"+normY);
            workIV = ivBig;
        } else {
            Log.e("BRul", "out of circle");
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
                if (workIV == ivSmall) {
                    moveSmallBeginTime = System.currentTimeMillis();
                    moveSmallBeginX = normX;
                    moveSmallBeginY = normY;
                } else if (workIV == ivBig) {
                    moveBigBeginTime = System.currentTimeMillis();
                    moveBigBeginX = normX;
                    moveBigBeginY = normY;
                }
                break;
            case MotionEvent.ACTION_MOVE: // движение
                if (workIV == ivSmall) {
                    currentMoveTime = System.currentTimeMillis();

                    time = currentMoveTime - moveSmallBeginTime;

                    angle = (float) Math.toDegrees(Math.atan((double)normY/normX)-Math.atan((double)moveSmallBeginY/moveSmallBeginX));
                    smallAVOTA += angle*1.6;
                    //loa = (int) smallAVOTA % halfAngle;
                    //loai = (int) smallAVOTA / halfAngle;
                    //resangle = loai * halfAngle + (loa<5 ? 0 : halfAngle);
                    resangle = (int) smallAVOTA;
                    newSmallLastAngle = smallLastAngle + resangle;

                    trip = Math.hypot(normX-moveSmallBeginX, normY-moveSmallBeginY);

                    endSmallMoveSpeed = smallMoveSpeed;
                    smallMoveSpeed = trip / time;

                    if (smallMoveSpeed > fastSpeed) {
                        time *= 6;
                    } else if (smallMoveSpeed > mediumSpeed) {
                        time *= 4;
                    } else if (smallMoveSpeed > lowSpeed) {
                        time *= 2;
                    }
                    Log.e("BRul", "angle: " + angle + "\tavota=" + smallAVOTA + "\tresangle=" + resangle + "\ttime: " + time + "\tx=" + x + "," + normX + "," + moveSmallBeginX + "\ty=" + y + "," + normY + "," + moveSmallBeginY + "\tmoveSpeed=" + smallMoveSpeed);
                    if (resangle != 0) {
                        //Log.e("BRul", "angle: " + angle + "\ttime: " + time + "\tx=" + x + "," + moveSmallBeginX + "\ty=" + y + "," + moveSmallBeginY + "\tavota=" + smallAVOTA + "\tloa=" + loa + "\tloai=" + loai + "\tresangle=" + resangle + "\tmoveSpeed=" + smallMoveSpeed);
                        //Log.e("BRul", "angle: " + angle + "\tavota=" + smallAVOTA + "\tresangle=" + resangle + "\ttime: " + time + "\tx=" + normX + "," + moveSmallBeginX + "\ty=" + normY + "," + moveSmallBeginY + "\tmoveSpeed=" + smallMoveSpeed);
                        ObjectAnimator anim = ObjectAnimator.ofFloat((View) workIV, "Rotation", smallLastAngle, newSmallLastAngle);
                        anim.setDuration(time);
                        anim.start();
                    }

                    smallAVOTA = 0;
                    moveSmallBeginX = normX;
                    moveSmallBeginY = normY;
                    smallLastAngle = newSmallLastAngle;
                    moveSmallBeginTime = currentMoveTime;
                } else if (workIV == ivBig) {
                }
                break;
            case MotionEvent.ACTION_UP: // отпускание
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
        /*
                double trip;
                double lastTrip;
                float angle;
                long currentMoveTime;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        moveBeginTime = System.currentTimeMillis();
                        moveBeginX = x;
                        moveBeginY = y;
                        //sDown = "Down: " + x + "," + y;
                        //sMove = ""; sUp = "";
                        break;
                    case MotionEvent.ACTION_MOVE: // движение
                        currentMoveTime = System.currentTimeMillis();
                        long time = currentMoveTime - moveBeginTime;
                        angle = (float) Math.toDegrees(Math.atan((double)y/x)-Math.atan((double)moveBeginY/moveBeginX));
                        avota += angle*1.6;
                        int loa = (int) avota % 9;
                        int loai = (int) avota / 9;
                        int resangle = loai * 9 + (loa<5 ? 0 : 9);
                        newLastAngle = bigLastAngle + resangle; //angle*2f;
                        trip = Math.sqrt(Math.pow(x-moveBeginX, 2)+Math.pow(y-moveBeginY, 2));

                        endMoveSpeed = moveSpeed;
                        moveSpeed = trip / time;

                        if (moveSpeed > 45) {
                            time *= 6;
                        }
                        if (moveSpeed > 30) {
                            time *= 4;
                        }
                        if (moveSpeed > 15) {
                            time *= 2;
                        }

                        //if (Math.abs(newLastAngle - bigLastAngle) > 9) {
                        //int moveAngle = ((int) (newLastAngle - bigLastAngle) / 9) * 9;
                        if (resangle != 0) {
                            Log.e("BRul", "angle: " + angle + "\ttime: " + time + "\tx=" + x + "," + moveBeginX + "\ty=" + y + "," + moveBeginY + "\tavota=" + avota + "\tloa=" + loa + "\tloai=" + loai + "\tresangle=" + resangle + "\tmoveSpeed=" + moveSpeed);
                            ObjectAnimator anim = ObjectAnimator.ofFloat((View) workIV, "Rotation", bigLastAngle, newLastAngle);
                            anim.setDuration(time);
                            anim.start();
                        }

                        avota = 0;
                        //Log.e("BRul", "avota=" + avota + "\tresangle=" + resangle);
                        moveY = y;
                        moveX = x;
                        bigLastAngle = newLastAngle;
                        moveBeginTime = currentMoveTime;
                        //}

                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        if (moveX != 0 || moveY != 0) {
                            Log.e("BRul", "moveX:"+moveX + "\tmoveY"+moveY+" ACTION_IP");
                            //lastTrip = Math.sqrt(Math.pow(x - moveX, 2) + Math.pow(y - moveY, 2));
                            moveEndTime = System.currentTimeMillis();
                            //endMoveSpeed = lastTrip / moveEndTime;

                            if (endMoveSpeed > moveSpeed) {
                                Log.e("BRul", "endMoveSpeed:" + endMoveSpeed + "\tmoveSpeed:" + moveSpeed);
                            } else {
                                Log.e("BRul", "moveSpeed:" + moveSpeed);
                            }
                        }

                        //newLastAngle = 9f*13*speed;
                    case MotionEvent.ACTION_CANCEL:
                        //sMove = "";
                        //sUp = "Up: " + x + "," + y;
                        break;
                }
                //tv.setText("\n Speed: " + speed);

        //if (x - moveBeginX > 0) {
            newLastAngle += bigLastAngle;
        //} else {
        //    newLastAngle -= bigLastAngle;
        //}
        if (bigLastAngle >= 360f) {
            //Log.e("BRul", "bLA-" + bigLastAngle);
            //bigLastAngle %= 360f;
            Log.e("BRul", "bLA-" + bigLastAngle);
        }
        if (newLastAngle >= 360f) {
            //Log.e("BRul", "nLA-" + newLastAngle);
            //newLastAngle %= 360f;
            Log.e("BRul", "nLA-" + newLastAngle);
        }
        if (newLastAngle < bigLastAngle) {
            //newLastAngle += 360f;
        }
        Log.e("BRul", "ANGLE:" + bigLastAngle + " -- " + newLastAngle);
        ObjectAnimator anim = ObjectAnimator.ofFloat((View) workIV, "Rotation", bigLastAngle, newLastAngle);
        anim.setDuration(time);
        //if (newLastAngle != 0) {
            anim.start();
            bigLastAngle = newLastAngle;
        //}
        //Animation anim = AnimationUtils.loadAnimation(this, R.anim.myrotate);
        //for (int i = 0; i < speed*4; ++i) {
            //tv.setText("i = " + i);
            //tv.startAnimation(anim);
        //}
        //        return true;

            //case R.id.ivSmall:
                //return true;
            //default:
                //return true;
        //}
        */
    }
}