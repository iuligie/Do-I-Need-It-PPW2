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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    Product current;
    private boolean hasNewImg=false;
    ProductApi api=ProductApi.getInstance();

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    // connection to firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Products");
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
        saveButton = findViewById(R.id.post_save_item_button);
        currentUserTextView = findViewById(R.id.post_username_textview);
        addPhotoButton = findViewById(R.id.postCameraButton);

        imageView = findViewById(R.id.post_imageView);

        saveButton.setOnClickListener(this);
        addPhotoButton.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);

        String itemPos = api.getCurrentItemPos();

        if(itemPos!=null)
        {

          current = api.getProductList().get(Integer.parseInt(itemPos));
            titleEdittext.setText(current.getTitle());
            reflectionEditText.setText(current.getPrice());
            //imageView.setImageURI(current.getImageUrl());
            //use picasso library to download and show image
            Picasso.get().load(current.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .fit()
                    .into(imageView);
            //imageUri= current.getImageUrl();
            saveButton.setText(R.string.Update);
        }

        if (ProductApi.getInstance() != null) {
            currentUserId = ProductApi.getInstance().getUserId();
            currentUserName = ProductApi.getInstance().getUsername();

            currentUserTextView.setText(currentUserName);
        }

        authStateListener = firebaseAuth -> {

            user = firebaseAuth.getCurrentUser();
           // if (user != null) {} else {}
        };


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.post_save_item_button:
                //save the reflection


                if(current!=null)
                updateItem(current);
                else{
                    saveItem();
                }
                break;

            case R.id.postCameraButton:
                //post an image

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*"); //anything that is image related
                startActivityForResult(galleryIntent, GALLERY_CODE);

                break;
        }
    }
    private void updateItem(final Product item){



            //region Image Handler
            final StorageReference filepath = storageReference.child("img_items").child("my_item_" + Timestamp.now().getSeconds());
            //making the image filenames unique with timestamp
            if (imageUri!=null && hasNewImg) {
                filepath.putFile(imageUri)
                        .addOnSuccessListener
                                (taskSnapshot -> filepath.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            String imageUrl = uri.toString();
                                            item.setImageUrl(imageUrl);
                                            handleDoc(item);
                                        })
                                        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()))
                                )
                        .addOnFailureListener(e -> progressBar.setVisibility(View.INVISIBLE));
            }else
            {
                handleDoc(item);
            }
//endregion
       // }




    }
    private void handleDoc(Product item)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String title = titleEdittext.getText().toString().trim();
        final String price = reflectionEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(price) ) {
            //region docHandler
            DocumentReference itemRef = db
                    .collection("Products")
                    .document(item.getItemId());

            itemRef.update(
                    "title", title,
                    "price", price,
                    "imageUrl", item.getImageUrl()
            ).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(PostProductActivity.this, "Item updated!", Toast.LENGTH_LONG).show();
                    api.setCurrentItemPos(null);
                    startActivity(new Intent(PostProductActivity.this, ProductListActivity.class));
                    finish();
                } else {
                    Toast.makeText(PostProductActivity.this, "Something went wrong - check log", Toast.LENGTH_LONG).show();
                }
            });
        }
        //endregion
    }
    private void saveItem() {

        final String title = titleEdittext.getText().toString().trim();
        final String price = "£" + reflectionEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(price) && imageUri != null) {
            //saving image
           final StorageReference filepath = storageReference.child("reflection_images").child("my_image_" + Timestamp.now().getSeconds());
            //making the image filenames unique with timestamp

            filepath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri -> {

                String imageUrl = uri.toString();

                // create journal object, invoke collectionRef, save journal instance

                Product product = new Product();
                product.setTitle(title);
                product.setPrice(price);
                product.setImageUrl(imageUrl);
                product.setTimeAdded(new Timestamp(new Date()));
                product.setItemId(currentUserName);
                product.setUserId(currentUserId);

                collectionReference.add(product).addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.INVISIBLE);

                    startActivity(new Intent(PostProductActivity.this, ProductListActivity.class));
                    finish();
                })
                        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage())))
                    .addOnFailureListener(e -> progressBar.setVisibility(View.INVISIBLE));
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Can not submit empty fields", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {

                imageUri = data.getData(); //we have the actual path
                imageView.setImageURI(imageUri); //show image
                if(current!=null)
                {
                    hasNewImg=true;
                }
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
        api.setCurrentItemPos(null);
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}

