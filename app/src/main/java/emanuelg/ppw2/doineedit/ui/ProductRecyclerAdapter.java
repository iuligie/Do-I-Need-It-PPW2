package emanuelg.ppw2.doineedit.ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import emanuelg.ppw2.doineedit.PostProductActivity;
import emanuelg.ppw2.doineedit.R;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import emanuelg.ppw2.doineedit.model.Product;
import emanuelg.ppw2.doineedit.util.ProductApi;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private ProductApi api=ProductApi.getInstance();

    public ProductRecyclerAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        api.setProductList(productList);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerAdapter.ViewHolder holder, int position) {

        Product product = productList.get(position);
        String imageUrl;

        holder.title.setText(product.getTitle());
        holder.price.setText(product.getPrice());
        holder.owned.setChecked(product.isOwned());
        //holder.name.setText(product.getUserName());
        imageUrl = product.getImageUrl();

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(product.getTimeAdded().getSeconds() *1000);
        holder.dateAdded.setText(timeAgo);

        //use picasso library to download and show image
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .fit()
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public Product getItemAt(int position){
        return productList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title, price, dateAdded, name;
        public ImageView image;
        public CheckBox owned;
       // String userId;
        //String username;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            title = itemView.findViewById(R.id.txtTitle);
            price = itemView.findViewById(R.id.txtPrice);
            dateAdded = itemView.findViewById((R.id.product_time_stamp));
            image = itemView.findViewById(R.id.img_item);
            owned=itemView.findViewById(R.id.owned_checkbox);

            owned.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Product item = getItemAt(getAdapterPosition());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference itemRef = db
                        .collection("Products")
                        .document(item.getItemId());

                itemRef.update("owned", isChecked
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ctx, "New item set to owned!", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(ctx, "Something went wrong - check log", Toast.LENGTH_LONG).show();
                    }
                });
            });
            //name = itemView.findViewById(R.id.username_account);
            itemView.setOnLongClickListener(v -> {
                Toast.makeText(v.getContext(), "Long Click Detected", Toast.LENGTH_LONG).show();
                //Intent intent = ;
                //intent.putExtra("itemPos", getAdapterPosition());
                ProductApi api=ProductApi.getInstance();
                api.setCurrentItemPos(Integer.toString(getAdapterPosition()));
                ctx.startActivity(new Intent(v.getContext(), PostProductActivity.class));
                return false;
            });


        }
    }
}
