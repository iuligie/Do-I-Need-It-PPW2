package emanuelg.ppw2.doineedit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import emanuelg.ppw2.doineedit.list.ProductListActivity;
import emanuelg.ppw2.doineedit.model.Product;
import emanuelg.ppw2.doineedit.util.ProductApi;

public class PostProductActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 1;
    private static final String TAG = "PostProductActivity";

    private Button saveButton;
    private ProgressBar progressBar;
    private ImageView addPhotoButton;
    private EditText titleEdittext;
    private EditText reflectionEditText;
    private TextView currentUserTextView;
    private TextView reflectionDate;
    private ImageView imageView;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    // connection to firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Reflection");
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_reflection);


        storageReference = FirebaseStorage.getInstance().getReference();


        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.post_progress_bar);
        titleEdittext = findViewById(R.id.post_title_et);
        reflectionEditText = findViewById(R.id.post_description_et);
        saveButton = findViewById(R.id.post_save_reflection_button);
        currentUserTextView = findViewById(R.id.post_username_textview);
        addPhotoButton = findViewById(R.id.postCameraButton);

        imageView = findViewById(R.id.post_imageView);

        saveButton.setOnClickListener(this);
        addPhotoButton.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);


        if (ProductApi.getInstance() != null) {
            currentUserId = ProductApi.getInstance().getUserId();
            currentUserName = ProductApi.getInstance().getUsername();

            currentUserTextView.setText(currentUserName);
        }

        authStateListener = firebaseAuth -> {

            user = firebaseAuth.getCurrentUser();
            if (user != null) {


            } else {


            }
        };


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.post_save_reflection_button:
                //save the reflection

                saveJournal();

                break;

            case R.id.postCameraButton:
                //post an image

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*"); //anything that is image related
                startActivityForResult(galleryIntent, GALLERY_CODE);

                break;
        }
    }

    private void saveJournal() {

        final String title = titleEdittext.getText().toString().trim();
        final String products = reflectionEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(products) && imageUri != null) {
            //saving image
            final StorageReference filepath = storageReference.child("reflection_images").child("my_image_" + Timestamp.now().getSeconds());
            //making the image filenames unique with timestamp

            filepath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    String imageUrl = uri.toString();

                    // create journal object, invoke collectionRef, save journal instance

                    Product product = new Product();
                    product.setTitle(title);
                    //product.setProduct(products);
                    product.setImageUrl(imageUrl);
                    product.setTimeAdded(new Timestamp(new Date()));
                    product.setUserName(currentUserName);
                    product.setUserId(currentUserId);

                    collectionReference.add(product).addOnSuccessListener(documentReference -> {
                        progressBar.setVisibility(View.INVISIBLE);

                        startActivity(new Intent(PostProductActivity.this, ProductListActivity.class));
                        finish();
                    }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
                }
            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()))).addOnFailureListener(e -> progressBar.setVisibility(View.INVISIBLE));
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); //we have the actual path
                imageView.setImageURI(imageUri); //show image
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}

