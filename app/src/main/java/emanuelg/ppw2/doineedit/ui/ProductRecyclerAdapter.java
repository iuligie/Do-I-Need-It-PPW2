package emanuelg.ppw2.doineedit.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import emanuelg.ppw2.doineedit.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import emanuelg.ppw2.doineedit.model.Product;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductRecyclerAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.reflection_row, parent, false);


        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerAdapter.ViewHolder holder, int position) {

        Product product = productList.get(position);
        String imageUrl;

        holder.title.setText(product.getTitle());
        holder.reflections.setText(product.getTitle());
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title, reflections, dateAdded, name;
        public ImageView image;
        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            title = itemView.findViewById(R.id.product_title_list);
            reflections = itemView.findViewById(R.id.product_description_list);
            dateAdded = itemView.findViewById((R.id.product_time_stamp));
            image = itemView.findViewById(R.id.product_image_list);
            //name = itemView.findViewById(R.id.username_account);


        }
    }
}
