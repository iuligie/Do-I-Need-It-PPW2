package emanuelg.ppw2.doineedit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import emanuelg.ppw2.doineedit.PostProductActivity;
import emanuelg.ppw2.doineedit.R;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
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
        view.findViewById(R.id.img_progressBar).setVisibility(View.INVISIBLE);
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
        holder.img_progressBar.setVisibility(View.VISIBLE);
        //use picasso library to download and show image
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .fit()
                .into(holder.image,new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.img_progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("IMG-ERROR","Something went wrong when loading the image");
                    }
                });

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
        public ProgressBar img_progressBar;
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
            img_progressBar=itemView.findViewById(R.id.img_progressBar);
            img_progressBar.setVisibility(View.VISIBLE);

            //Product item = getItemAt(getAdapterPosition());
            itemView.setOnClickListener(v -> new AlertDialog.Builder(itemView.getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("This action will be opened in another application!")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String item_url=getItemAt(getAdapterPosition()).getItemUrl();
                            if(item_url==null ){Log.d("URL","URL is NULL");}
                            else
                            {if(!(item_url.startsWith("http://") || item_url.startsWith("https://")))
                            {
                                item_url="http://" + item_url;
                            }
                            Uri uri = Uri.parse(item_url); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            ctx.startActivity(intent);}
                        }

                    })
                    .setNegativeButton("Cancel", null)
                    .show());

            owned.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Product item = getItemAt(getAdapterPosition());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference itemRef = db
                        .collection("Products")
                        .document(item.getItemId());

                itemRef.update("owned", isChecked
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                       // Toast.makeText(ctx, "New item set to owned!", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(ctx, "Something went wrong - check log", Toast.LENGTH_LONG).show();
                    }
                });
            });

            itemView.setOnLongClickListener(v -> {
                Toast.makeText(v.getContext(), "Long Press Detected - Edit Item", Toast.LENGTH_LONG).show();
                ProductApi api=ProductApi.getInstance();
                api.setCurrentItemPos(Integer.toString(getAdapterPosition()));
                ctx.startActivity(new Intent(v.getContext(), PostProductActivity.class));
                return false;
            });


        }
    }
}
