package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.CategoryRecyclerViewAdpter;
import com.adityarana.sangharsh.learning.sangharsh.Model.User;
import com.adityarana.sangharsh.learning.sangharsh.Tools.OrderRepository;
import com.adityarana.sangharsh.learning.sangharsh.Model.Category;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.OrderResponse;
import com.adityarana.sangharsh.learning.sangharsh.Model.SubCategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements CategoryRecyclerViewAdpter.Listener, PaymentResultListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    private HomeCategory homeCategory;
    private Category category;
    private TextView textView;
    private Button buyNowBtn;
    private OrderRepository orderRepository;
    private Boolean isPurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        String strHome = getIntent().getStringExtra("HOME_CATEGORY");
        isPurchased = getIntent().getBooleanExtra("PURCHASED", false);
        if (!isPurchased){
            orderRepository = orderRepository.getInstance();
            setBuyBtn();
        }
        textView = findViewById(R.id.contentSoonText);
        progressBar = findViewById(R.id.progressBar);
        if (strHome.isEmpty()){
            progressBar.setVisibility(View.GONE);
            textView.setText("Some error occured");
        } else {
            homeCategory = new Gson().fromJson(strHome, HomeCategory.class);
            if (homeCategory.getSubcat() <= 0){
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                TextView titleTxt = findViewById(R.id.titleTxt);
                titleTxt.setText(homeCategory.getName());
                requestData();
            }
        }
    }

    private void setBuyBtn() {
        buyNowBtn = findViewById(R.id.buyNowBtn);
        buyNowBtn.setVisibility(View.VISIBLE);
        ConstraintLayout layout = findViewById(R.id.mainContraintView);
        layout.setPadding(0, 0 , 0, 64);
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderParams params = new OrderParams();
                progressBar.setVisibility(View.VISIBLE);
                orderRepository.getPurchaseService().createOrder(params).enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        if (response.body() != null) {
                            startPayment(response.body());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CategoryActivity.this, "There was some problem with your request! Please contact Sangharsh Team", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        Toast.makeText(CategoryActivity.this, "Some unexpected error occurred! Please contact Sangharsh Team", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void requestData() {
        FirebaseFirestore.getInstance()
                .collection("Categories")
                .document(homeCategory.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                category = task.getResult().toObject(Category.class);
                                setUpRecyclerView();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                textView.setText("Requested document is missing");
                                textView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            textView.setText("Data fecting error");
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void startPayment(OrderResponse response) {

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_4DOylsb3vNceFP");

        //TODO: Set Logo
        checkout.setImage(R.mipmap.ic_launcher);

        final Activity activity = this;
        Log.i("amount", Integer.toString(response.getAmount()));
//        Log.i("receipt", response.getReceipt());

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Sangharsh");
            options.put("description", "Course - " + homeCategory.getName());
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", response.getId());//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", response.getCurrency());
            options.put("amount", response.getAmount());//pass amount in currency subunits
            checkout.open(activity, options);
        } catch(Exception e) {
            Toast.makeText(activity, "Some error occurred! Please contact Sangharsh Team", Toast.LENGTH_SHORT).show();
            Log.e("checkoutError", "Error in starting Razorpay Checkout", e);
        }
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        CategoryRecyclerViewAdpter adapter = new CategoryRecyclerViewAdpter(category.getSubcategories(), this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openLectures(SubCategory subCategory) {
        Intent intent = new Intent(this, LecturesActivity.class);
        intent.putExtra("IS_TOPIC", false);
        intent.putExtra("SUB_CATEGORY", new Gson().toJson(subCategory));
        intent.putExtra("PURCHASED", isPurchased);
        startActivity(intent);
    }

    @Override
    public void showTopics(SubCategory subCategory) {
        Intent intent = new Intent(this, TopicsActivity.class);
        intent.putExtra("SUB_CATEGORY", new Gson().toJson(subCategory));
        intent.putExtra("PURCHASED", isPurchased);
        startActivity(intent);
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Test payment done!", Toast.LENGTH_SHORT).show();
        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance()
                .getCurrentUser().getUid())
                .update("purchasedCourses", FieldValue.arrayUnion(homeCategory.getId()))
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    buyNowBtn.setVisibility(View.GONE);
                    isPurchased = true;
                    CategoryActivity.this.getSharedPreferences("MY_PREF", MODE_PRIVATE).edit()
                            .putString("PURCHASED_NOW", homeCategory.getId()).apply();
                } else {
                    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                    User user = new User();
                    ArrayList<String> purchased = new ArrayList<String>();
                    purchased.add(homeCategory.getId());
                    user.setPurchasedCourses(purchased);
                    user.setUid(mUser.getUid());
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(user.getUid())
                            .set(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> newTask) {
                                    if (newTask.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        buyNowBtn.setVisibility(View.GONE);
                                        isPurchased = true;
                                        CategoryActivity.this.getSharedPreferences("MY_PREF", MODE_PRIVATE).edit()
                                                .putString("PURCHASED_NOW", homeCategory.getId()).apply();
                                    } else {
                                        Toast.makeText(CategoryActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }



    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Test payment failed!" + s, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        Log.i("paymentError:", s);
    }

    public class OrderParams{
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String CourseID = homeCategory.getId();
    }

}