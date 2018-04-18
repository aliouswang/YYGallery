package com.hmzl.aliouswang.yygallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private ScaleImageView img_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_share = findViewById(R.id.img_share);

        Button btn_share = findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gotoShare();
            gotoImageScale();
            }
        });
    }

    private void gotoImageScale() {
        int[] location = new int[2];
        img_share.getLocationOnScreen(location);
        int width = img_share.getWidth();
        int height = img_share.getHeight();
        ImageScaleActivity.jump(this, location[0], location[1], width, height,
                img_share.getRatio());
        this.overridePendingTransition(0, 0);
    }


    private void gotoShare() {
        Intent intent = new Intent(MainActivity.this, ShareActivity.class);
        startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, img_share, "share"
                ).toBundle());
    }
}
