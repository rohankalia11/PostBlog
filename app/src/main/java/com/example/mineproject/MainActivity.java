package com.example.mineproject;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.InetAddress;


public class MainActivity extends AppCompatActivity {

    Toolbar mainToolbar;
    FirebaseAuth mAuth;
    FloatingActionButton addpostbtn;


    HomeFragment homeFragment;
    NotificationFragment notificationFragment;
    AccountFragment accountFragment;

    FirebaseFirestore firebaseFirestore;


    String user_id,Accountname;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();




        mainToolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("BlogPost");


        homeFragment =new HomeFragment();
        notificationFragment=new NotificationFragment();
        accountFragment=new AccountFragment();

        firebaseAuth =FirebaseAuth.getInstance();

        user_id=firebaseAuth.getCurrentUser().getUid();

        addpostbtn=findViewById(R.id.fab);








        AHBottomNavigation bottomNavigation =  findViewById(R.id.bottom_navigation);

// Create items
        final AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.bottom_home_text,R.mipmap.action_home, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.bottom_ac_text, R.mipmap.action_ac, R.color.colorAccent);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.bottom_notification_text, R.mipmap.action_about, R.color.colorAccent);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setBackgroundColor(Color.parseColor("#212121"));

        bottomNavigation.setAccentColor(Color.parseColor("#ff0000"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));



        replaceFragment(homeFragment);














       bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
           @Override
           public boolean onTabSelected(int position, boolean wasSelected) {

               switch (position)
               {
                   case 0:

                       replaceFragment(homeFragment);


                       return true;

                   case 1:

                       replaceFragment(accountFragment);





                       return true;
                   case 2:

                       replaceFragment(notificationFragment);



                       return true;

                       default:
                           return false;
               }

           }
       });







    }
    @Override
    protected void onStart() {
        super.onStart();
      /* FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentuser == null)
        {
           sendToLogin();
        }*/


      /*  firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {


                        Accountname = task.getResult().getString("Account Type");

                        if (Accountname.equals("Student"))
                        {

                            addpostbtn.hide(true);

                        }


                    }
                }
            }
        });*/

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);


        return true;



    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_logout_button:


                logout();
                return true;


            case R.id.action_settings_button:

                Intent settingIntent = new Intent(MainActivity.this,SetupActivityDumy.class);
                startActivity(settingIntent);
                return true;

            default:
                return false;
        }



    }

    private void logout() {

        mAuth.signOut();
        sendToLogin();
    }


    @Override
    public void onBackPressed() {

        finishAffinity();
    }



    private void sendToLogin() {

        Intent intent = new Intent(MainActivity.this,OnStart.class);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment fragment)
    {

        FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
    }






}


