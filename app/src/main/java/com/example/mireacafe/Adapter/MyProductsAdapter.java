package com.example.mireacafe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mireacafe.EventBus.MyUpdateCartEvent;
import com.example.mireacafe.Listener.CartLoadListener;
import com.example.mireacafe.Model.CartModel;
import com.example.mireacafe.Model.ProductModel;
import com.example.mireacafe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsViewHolder> {
    private Context context;
    private List<ProductModel> productModelList;
    private CartLoadListener cartLoadListener;

    public MyProductsAdapter(Context context, List<ProductModel> productModelList, CartLoadListener cartLoadListener) {
        this.context = context;
        this.productModelList = productModelList;
        this.cartLoadListener = cartLoadListener;
    }

    @NonNull
    @Override
    public MyProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyProductsViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyProductsViewHolder holder, int position) {
        Glide.with(context).load(productModelList.get(position).getImage()).into(holder.imageView);
        holder.txtProductPrice.setText(new StringBuilder("₽ ").append(productModelList.get(position).getPrice()));
        holder.txtProductName.setText(new StringBuilder().append(productModelList.get(position).getPname()));

        holder.setListener((view, adapterPosition) -> {
            addToCart(productModelList.get(position));

        });

    }

    private void addToCart(ProductModel productModel) {
        DatabaseReference userCart = FirebaseDatabase.getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID");

        userCart.child(productModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity()+1);
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("quantity", cartModel.getQuantity());
                            updateData.put("totalPrice",  cartModel.getQuantity()*Float
                                    .parseFloat(cartModel.getPrice()));
                            userCart.child(productModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        cartLoadListener.onCartLoadFailed("Добавлено в корзину");


                                    })
                            .addOnFailureListener(e -> cartLoadListener.onCartLoadFailed(e.getMessage()));

                        }
                        else {
                            CartModel cartModel = new CartModel();
                            cartModel.setName(productModel.getPname());
                            cartModel.setImage(productModel.getImage());
                            cartModel.setKey(productModel.getKey());
                            cartModel.setPrice(productModel.getPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(productModel.getPrice()));

                            userCart.child(productModel.getKey())
                                    .setValue(cartModel)
                                    .addOnSuccessListener(aVoid -> {
                                        cartLoadListener.onCartLoadFailed("Add to cart success");


                                    })
                                    .addOnFailureListener(e -> cartLoadListener.onCartLoadFailed(e.getMessage()));




                        }

                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());

                    }
                });
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

}
