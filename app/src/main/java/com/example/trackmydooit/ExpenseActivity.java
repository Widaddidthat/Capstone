package com.example.trackmydooit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView expenseTV;
    private RecyclerView RVExpense;

    private ExtendedFloatingActionButton FABAddTrans;

    private FirebaseAuth mAuth;
    private DatabaseReference expenseRef;
    private String onlineUserId = "";
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference().child("expense").child(onlineUserId);
        loader = new ProgressDialog(this);

        expenseTV = findViewById(R.id.expenseTV);
        RVExpense = findViewById(R.id.RVExpense);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        RVExpense.setHasFixedSize(true);
        RVExpense.setLayoutManager(linearLayoutManager);

        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;

                for (DataSnapshot snap: snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalAmount = totalAmount + data.getAmount();
                    String sTotal = "Expenses this month: RM" + totalAmount;
                    expenseTV.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FABAddTrans = findViewById(R.id.FABAddTrans);

        FABAddTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpentOn();
            }
        });
    }

    private void addItemSpentOn() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout_expense, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner spinnerExpense = myView.findViewById(R.id.spinnerExpense);
        final EditText ExpenseAmountET = myView.findViewById(R.id.AmountET);
        final Button cancelTransBTN = myView.findViewById(R.id.cancelTransBTN);
        final Button addTransBTN = myView.findViewById(R.id.addTransBTN);

        addTransBTN.setOnClickListener(view -> {
            String expenseAmount = ExpenseAmountET.getText().toString();
            String expenseItem = spinnerExpense.getSelectedItem().toString();

            if (TextUtils.isEmpty(expenseAmount)){
                ExpenseAmountET.setError("Amount is required!");
                return;
            }

            if(expenseItem.equals("Select an expense category")){
                Toast.makeText(ExpenseActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
            }

            else {
                loader.setMessage("Adding an expense");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                String id = expenseRef.push().getKey();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months months = Months.monthsBetween(epoch, now);

                Data data = new Data(expenseItem, date, id, null, Integer.parseInt(expenseAmount), months.getMonths());
                expenseRef.child(id).setValue(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(ExpenseActivity.this, "Expense item added successfully!", Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Toast.makeText(ExpenseActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                    loader.dismiss();
                });

            }
            dialog.dismiss();
        });

        cancelTransBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(expenseRef, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemAmount("Allocated amount: RM" + model.getAmount());
                holder.setDate("On" + model.getDate());
                holder.setItemName("Expense Item: " + model.getItem());

                holder.notes.setVisibility(View.GONE);

                switch (model.getItem()){
                    case "Rent":
                        holder.itemIV.setImageResource(R.drawable.ic_home);
                        break;
                    case "Utilities":
                        holder.itemIV.setImageResource(R.drawable.water_drop_fill0_wght300_grad0_opsz40);
                        break;
                    case "Dental":
                        holder.itemIV.setImageResource(R.drawable.dentistry_fill1_wght300_grad0_opsz40);
                        break;
                    case "Transportation":
                        holder.itemIV.setImageResource(R.drawable.directions_bus_fill1_wght300_grad0_opsz40);
                        break;

                }

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout_budget, parent, false);
                return new MyViewHolder(view);
            }
        };

        RVExpense.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public ImageView itemIV;
        public TextView notes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            itemIV = itemView.findViewById(R.id.itemIV);
            notes = itemView.findViewById(R.id.note);
        }

        public void setItemName (String itemName){
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount (String itemAmount){
            TextView item = mView.findViewById(R.id.amount);
            item.setText(itemAmount);
        }

        public void setDate (String itemDate){
            TextView item = mView.findViewById(R.id.date);
        }
    }
}