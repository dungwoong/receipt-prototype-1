package com.example.dashboardapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dashboardapp.receipts.Receipt;
import com.example.dashboardapp.receipts.ReceiptManager;
import com.example.dashboardapp.receipts.ReceiptViewer;

import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends AppCompatActivity {

    ReceiptManager receiptManager = ReceiptManager.getInstance();
    ReceiptViewer receiptViewer = ReceiptViewer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        mapReceiptToScreen();
    }

    /**
     * Makes the textview editable and vice versa
     * @param view
     */
    public void editTextView(View view) {
        // Maybe consider using tags for this part and not ids?
        TextView textView = view.findViewWithTag("text_5");
        EditText editText = view.findViewById(R.id.inside_edittext);
        if (textView.getVisibility() == View.VISIBLE) {
            Log.i("Viewing Mode", "Editable Mode");
            editText.setText(textView.getText().toString());
            textView.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            editText.setFocusable(true);
            editText.requestFocus();
        } else {
            Log.i("Viewing Mode", "Text Mode");
            textView.setText(editText.getText().toString());
            editText.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            mapScreenToReceipt();
        }

    }

    public void dateDialogue(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ThirdActivity.this);
        View alertView = getLayoutInflater().inflate(R.layout.date_set_dialog, null);
        builder.setView(alertView);
        DatePicker datePicker = alertView.findViewWithTag("date_input_indiv");
        Receipt r = getViewingReceipt();
        datePicker.updateDate(r.getYear(), r.getMonth(), r.getDay());

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                r.setYear(year);
                r.setMonth(month);
                r.setDay(day);
                mapReceiptToScreen();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Makes all textviews non-editable
     * @param view
     */
    public void editAllTextViews(View view) {
        LinearLayout name = view.findViewById(R.id.item_name);
        LinearLayout price = view.findViewById(R.id.price_item);
        LinearLayout info = view.findViewById(R.id.info_item);
        LinearLayout[] list = new LinearLayout[] {name, price, info};
        for (LinearLayout item : list) {
            if (item.getTag().toString().equals("editable_field") &&
                    item.findViewById(R.id.inside_edittext).getVisibility() == View.VISIBLE) {
                editTextView(item);
            }
        }
    }

    // ON LOAD functions
    public Receipt getViewingReceipt(){
        int id = this.getReceiptViewer().getViewingReceiptID();
        return this.getReceiptManager().getReceipt(id);
    }

    public void mapReceiptToScreen() {
        TextView title = findViewById(R.id.title);
        TextView price = findViewById(R.id.price);
        TextView description = findViewById(R.id.description);
        TextView date = findViewById(R.id.receipt_date);
        Receipt receipt = this.getViewingReceipt();

        String month = receipt.getMonth() < 9 ? "0" + Integer.toString(receipt.getMonth() + 1) :
                Integer.toString(receipt.getMonth() + 1);
        String day = receipt.getDay() < 9 ? "0" + Integer.toString(receipt.getDay()) :
                Integer.toString(receipt.getDay());
        String dateString = month + "/" +
                day + "/" + Integer.toString(receipt.getYear());
        title.setText(receipt.getTitle());
        price.setText(receipt.getAmount());
        description.setText(receipt.getDescription());
        date.setText(dateString);
    }

    public void mapScreenToReceipt() {
        TextView title = findViewById(R.id.title);
        TextView price = findViewById(R.id.price);
        TextView description = findViewById(R.id.description);
        TextView date = findViewById(R.id.receipt_date);
        Receipt receipt = this.getViewingReceipt();

        String[] dayOutput = date.getText().toString().split("/");
        // MM / DD / YYYY
        int day = Integer.parseInt(dayOutput[1]);
        int month = Integer.parseInt(dayOutput[0]) - 1;
        int year = Integer.parseInt(dayOutput[2]);

        receipt.setTitle(title.getText().toString());
        receipt.setAmount(price.getText().toString());
        receipt.setDescription(description.getText().toString());
        receipt.setDay(day);
        receipt.setMonth(month);
        receipt.setYear(year);
    }

    public void goBack(View view) {
        // TODO SAVE ALL INFO
        View allView = findViewById(R.id.all_receipt_fields);
        editAllTextViews(allView);
        mapScreenToReceipt();
        // TODO TURN VIEWING RECEIPT TO NULL?
        Intent intent = new Intent();
        setResult(getViewingReceipt().getId(), intent);
        finish();
    }

    public ReceiptManager getReceiptManager() {
        return this.receiptManager;
    }

    public ReceiptViewer getReceiptViewer() {
        return this.receiptViewer;
    }
}