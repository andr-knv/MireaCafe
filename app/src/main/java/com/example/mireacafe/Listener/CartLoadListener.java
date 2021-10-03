package com.example.mireacafe.Listener;

import com.example.mireacafe.Model.CartModel;

import java.util.List;

public interface CartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
