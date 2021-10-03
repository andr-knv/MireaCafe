package com.example.mireacafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AdminCategoryActivity extends AppCompatActivity {
    TextView  fastFood, firstMils, secondMils, sweets, drinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        fastFood = (TextView) findViewById(R.id.fast_food);
        firstMils = (TextView) findViewById(R.id.first_mils);
        secondMils = (TextView) findViewById(R.id.second_mils);
        sweets = (TextView) findViewById(R.id.sweets);
        drinks = (TextView) findViewById(R.id.drinks);

        fastFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "fastfood");
                startActivity(intent);


            }
        });

        firstMils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "first_mils");
                startActivity(intent);


            }
        });

        secondMils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "second_mils");
                startActivity(intent);


            }
        });

        sweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "sweets");
                startActivity(intent);


            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "drinks");
                startActivity(intent);


            }
        });
    }
}