package com.example.mireacafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.mireacafe.Adapter.MyProductsAdapter;
import com.example.mireacafe.EventBus.MyUpdateCartEvent;
import com.example.mireacafe.Listener.CartLoadListener;
import com.example.mireacafe.Listener.ProductLoadListener;
import com.example.mireacafe.Model.CartModel;
import com.example.mireacafe.Model.ProductModel;
import com.example.mireacafe.Utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;
import com.rey.material.widget.ImageButton;
import com.rey.material.widget.SnackBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements ProductLoadListener, CartLoadListener {
    @BindView(R.id.recyclerProducts)
    RecyclerView recyclerProducts;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.cartBtnFrame)
    FrameLayout cartBtnFrame;
    @BindView(R.id.logoutBtn)
    ImageView logout;

    ProductLoadListener productLoadListener;
    CartLoadListener cartLoadListener;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {

        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void  onUpdateCart(MyUpdateCartEvent event){
        countCartItem();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        productLoadListener = this;
        cartLoadListener = this;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerProducts.setLayoutManager(gridLayoutManager);
        recyclerProducts.addItemDecoration(new SpaceItemDecoration());



        loadProductFromFirebase();
        countCartItem();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                FirebaseDatabase.getInstance().getReference("Cart")
                        .child("UNIQUE_USER_ID")
                        .removeValue();

                Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(logoutIntent);
            }
        });
        cartBtnFrame.setOnClickListener(v -> startActivity(new Intent(this,CartActivity.class)));
    }

    private void loadProductFromFirebase() {
        List<ProductModel> productModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot productSnapshot:snapshot.getChildren()){
                        ProductModel productModel = productSnapshot.getValue(ProductModel.class);
                        productModel.setKey(productSnapshot.getKey());
                        productModels.add(productModel);
                    }
                    productLoadListener.onProductLoadSuccess(productModels);
                }
                else {
                    productLoadListener.onProductsLoadFailed("Невозможно найти товар");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                productLoadListener.onProductsLoadFailed(error.getMessage());

            }
        });
    }

    @Override
    public void onProductLoadSuccess(List<ProductModel> productModelList) {
        MyProductsAdapter adapter = new MyProductsAdapter(this, productModelList, cartLoadListener);
        recyclerProducts.setAdapter(adapter);

    }

    @Override
    public void onProductsLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum = 0;
        for(CartModel cartModel: cartModelList)
            cartSum += cartModel.getQuantity();
        badge.setNumber(cartSum);

    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot cartSnapshot:snapshot.getChildren()){
                            CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());

                    }
                });
    }
}