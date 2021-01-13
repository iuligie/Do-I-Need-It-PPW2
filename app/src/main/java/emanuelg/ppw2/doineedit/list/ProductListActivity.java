package emanuelg.ppw2.doineedit.list;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    private CollectionReference collectionReference = db.collection("Products");
    private TextView noItemEntry;
    ProductApi api=ProductApi.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        productList = new ArrayList<>();
        noItemEntry = findViewById(R.id.list_no_item);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Product.delete();

                //productRecyclerAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                if(direction==ItemTouchHelper.LEFT)
                {
                    View view;
                    sendSMS(productRecyclerAdapter.getItemAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(ProductListActivity.this, "Swipe Left", Toast.LENGTH_LONG).show();
                }
                if(direction==ItemTouchHelper.RIGHT)
                {
                    api.deleteItem(productRecyclerAdapter.getItemAt(viewHolder.getAdapterPosition()).getItemId());
                    productList.remove(viewHolder.getAdapterPosition());
                    //recyclerView.removeViewAt(viewHolder.getAdapterPosition());
                    productRecyclerAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    //productRecyclerAdapter.notifyDataSetChanged();
                    Toast.makeText(ProductListActivity.this, "Swipe Right - Delete", Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(ProductListActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    void sendSMS(Product item)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:07904868145"));
        intent.putExtra("sms_body","Hey! Check out this product! \n-> "+ item.getTitle() + " <-\n" + "It is only $" + item.getPrice() + "!");
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivity(intent);
        }
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
                //take user to add items
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
        UpdateItems();

    }

    private void UpdateItems()
    {
       productList.clear();
        collectionReference.whereEqualTo("userId", ProductApi.getInstance().getUserId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot products : queryDocumentSnapshots){
                        Product product = products.toObject(Product.class);
                        product.setItemId(products.getId());
                        productList.add(product);
                    }

                    //invoke recyler view
                    productRecyclerAdapter = new ProductRecyclerAdapter(ProductListActivity.this, productList);
                    recyclerView.setAdapter(productRecyclerAdapter);
                    productRecyclerAdapter.notifyDataSetChanged();
                }
                else{
                    noItemEntry.setVisibility(View.VISIBLE);
                }


            })
            .addOnFailureListener(e -> Log.d("DB-LOG", "onFailure: " + e.getMessage()));

    }
}
