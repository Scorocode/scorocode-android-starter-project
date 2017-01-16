package com.peterstaranchuk.scorocodetemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddDocumentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, AddDocumentActivity.class));
    }
}
