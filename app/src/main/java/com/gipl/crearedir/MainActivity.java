package com.gipl.crearedir;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    TextView textView;
    DocumentFile pickedDir;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv_msg);
        String uri = mPrefs.getString("PREF_TREE_URI", "");
        if (!uri.isEmpty()) {
            pickedDir = DocumentFile.fromTreeUri(this, Uri.parse(uri));
            textView.setText("Access is found for directory " + pickedDir.getName());
        } else {
            textView.setText("Access is not found for any directory");
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 42);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                if (pickedDir != null) {
                    DocumentFile documentFile = pickedDir.findFile("My Space");
                    if (documentFile == null)
                        documentFile = pickedDir.createDirectory("My Space");
                    DocumentFile newFile = documentFile.createFile("image/jpg", "My Car");
                    if (newFile == null)
                        textView.setText("Unable to create File");
                    else
                        textView.setText("File has been successfully created");

                } else {
                    textView.setText("Unable to create File");
                }
                //New Method
//                StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
//                StorageVolume storageVolume = storageManager.getPrimaryStorageVolume();
//                Intent intent = storageVolume.createOpenDocumentTreeIntent();
//                startActivityForResult(intent,123);

//old method
//                File samFile = new File(Environment.getExternalStorageDirectory() + "/MyDire/");
//                if (samFile.exists())
//                    samFile.delete();
//                if (samFile.mkdirs())
//                    textView.setText(samFile.getPath() + " has been created successfully");
//                else
//                    textView.setText(samFile.getPath() + " failed to create");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 42) {
            if (resultCode == RESULT_OK) {
                Uri treeUri = resultData.getData();
                mPrefs.edit().putString("PREF_TREE_URI", treeUri.toString()).apply();
                pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

                // List all existing files inside picked directory
                for (DocumentFile file : pickedDir.listFiles()) {
                    Log.d(TAG, "Found file " + file.getName() + " with size " + file.length());
                }


            }
        }
    }
}
