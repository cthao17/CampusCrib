package com.cs407.campuscrib;

import androidx.appcompat.app.AppCompatActivity;

import com.cs407.campuscrib.model.UserModel;
import com.cs407.campuscrib.utils.FirebaseUtil;
import com.cs407.campuscrib.utils.AndroidFunctionsUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getIntent().getExtras()!=null){
            //from notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            UserModel model = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this,homePage.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, ChattingActivity.class);
                            AndroidFunctionsUtil.passUsername(intent, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.startActivity(intent);
                            finish();
                        }
                    });


        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this,homePage.class));
                    }else{
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }
                    finish();
                }
            },1000);
        }
    }
}