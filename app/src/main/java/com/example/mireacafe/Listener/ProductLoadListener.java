package com.example.mireacafe.Listener;

import com.example.mireacafe.Model.ProductModel;

import java.util.List;

public interface ProductLoadListener {
    void onProductLoadSuccess(List<ProductModel> productModelList);
    void onProductsLoadFailed(String message);
}
