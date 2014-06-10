package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends Activity implements Runnable {

    SurfaceView surfaceView;
    SurfaceHolder holder;
    Thread thread;
    Paint p;
    Path path;
    List<Star> stars;
    Drawable logo;
    String text = "### Koen Hendriks & Corné Dorrestijn proudly present you.... Deltion Rooster App!!! ###";
    int frame = 0;
    int touchCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    private void startAnim() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();

        thread = new Thread(this);

        p = new Paint();
        p.setColor(getResources().getColor(android.R.color.black));
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(80);
        p.setAlpha(255);
        p.setTypeface(Typeface.MONOSPACE);
        p.setAntiAlias(false);

        path = new Path();
        path.moveTo(0, 150);
        for (int i = 0; i < 120; i++) {
            path.lineTo(i * 10, 150 + (float) (Math.sin(i / 10f) * 50f));
        }

        logo = getResources().getDrawable(R.drawable.shb);
        logo.setBounds(0, 0, logo.getIntrinsicWidth(), logo.getIntrinsicHeight());

        String spacing = "--------------------";
        this.text = spacing + text + spacing;

        stars = new ArrayList<Star>();

        thread.start();
    }

    public void easterEggClick(View view) {
        System.out.println("Hello World!");
        touchCount++;
        if(touchCount == 3) {
            startAnim();
            view.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (!holder.getSurface().isValid()) {
                continue;
            }

            Canvas canvas = holder.lockCanvas();

            if (canvas != null) {
                frame += 5;

                // clear canvas
                p.setColor(0x70000000);
                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), p);

                // render stars
                stars.add(new Star(canvas.getWidth() / 2, canvas.getHeight() / 2));

                p.setColor(0xFFFFFFFF);
                List<Star> trashcan = new ArrayList<Star>();

                for (int i = 0; i < stars.size(); i++) {
                    Star star = stars.get(i);
                    star.update();
                    p.setAlpha((int) (star.radius * 100 > 200 ? 200 : star.radius * 50));
                    canvas.drawCircle((float) star.x, (float) star.y, (float) star.radius, p);
                    if (star.x < 0 || star.x > canvas.getWidth() || star.y < 0 || star.y > canvas.getHeight()) {
                        trashcan.add(star);
                    }
                }

                for (int i = 0; i < trashcan.size(); i++) {
                    stars.remove(trashcan.get(i));
                }

                // render text

                p.setColor(0xFFFFFFFF);
                int offset = Math.round(frame / 50) % (text.length() - 17);
                String stext = text.substring(offset, offset + 17);
                canvas.drawTextOnPath(stext, path, -(frame % 50), 40, p);

                // render logo

                canvas.save();

                canvas.translate((canvas.getWidth() / 2) - (logo.getIntrinsicWidth() / 2), (canvas.getHeight() / 2) - (logo.getIntrinsicHeight() / 2));
                canvas.scale((float) (1.0 + (Math.sin(frame / 160f) / 2)), (float) (1.0 + (Math.cos(frame / 160f) / 2)), (logo.getIntrinsicWidth() / 2), (logo.getIntrinsicHeight() / 2));

                logo.draw(canvas);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    class Star {
        public double x = 0;
        public double y = 0;
        public double radius = 0;
        private double speedX = 0;
        private double speedY = 0;

        Star(double x, double y) {
            this.x = x;
            this.y = y;
            this.speedX = Math.sin(Math.random() * (Math.PI * 2));
            this.speedY = Math.cos(Math.random() * (Math.PI * 2));
        }

        public void update() {
            this.speedX *= 1.05;
            this.speedY *= 1.05;
            this.x += this.speedX;
            this.y += this.speedY;
            radius += (Math.abs(speedX) + Math.abs(speedY)) / 100;
        }
    }
}

