package emanuelg.ppw2.doineedit.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import emanuelg.ppw2.doineedit.MainActivity;
import emanuelg.ppw2.doineedit.PostProductActivity;
import emanuelg.ppw2.doineedit.R;
import emanuelg.ppw2.doineedit.model.Product;
import emanuelg.ppw2.doineedit.ui.ProductRecyclerAdapter;
import emanuelg.ppw2.doineedit.util.ProductApi;

public class ProductListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private List<Product> productList;
    private RecyclerView recyclerView;
    private ProductRecyclerAdapter productRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Product");
    private TextView noReflectionEntry;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflection_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        productList = new ArrayList<>();
        noReflectionEntry = findViewById(R.id.list_no_reflection);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_add:
                //take user to add reflection
                if (user != null && firebaseAuth != null){
                    startActivity(new Intent(ProductListActivity.this, PostProductActivity.class));
                    //finish();
                }
                break;
            case R.id.action_signout:
                //sign out
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(ProductListActivity.this, MainActivity.class));
                    //finish();
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", ProductApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot products : queryDocumentSnapshots){
                        Product product = products.toObject(Product.class);
                        productList.add(product);
                    }

                    //invoke recyler view
                    productRecyclerAdapter = new ProductRecyclerAdapter(ProductListActivity.this, productList);
                    recyclerView.setAdapter(productRecyclerAdapter);
                    productRecyclerAdapter.notifyDataSetChanged();
                }
                else{
                    noReflectionEntry.setVisibility(View.VISIBLE);
                }


                })
                .addOnFailureListener(e -> {

                });
    }
}
