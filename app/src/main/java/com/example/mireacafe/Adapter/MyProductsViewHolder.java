package com.example.mireacafe.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mireacafe.Listener.RecyclerViewClickListener;
import com.example.mireacafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.txtProductName)
    TextView txtProductName;
    @BindView(R.id.txtProductPrice)
    TextView txtProductPrice;

    RecyclerViewClickListener listener;

    public void setListener(RecyclerViewClickListener listener) {
        this.listener = listener;
    }

    private Unbinder unbinder;

    public MyProductsViewHolder(@NonNull View itemView) {
        super(itemView);
        unbinder = ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onRecyclerClick(v, getAdapterPosition());
    }
}
