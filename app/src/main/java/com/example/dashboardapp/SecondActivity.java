package com.example.dashboardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dashboardapp.receipts.Receipt;
import com.example.dashboardapp.receipts.ReceiptManager;
import com.example.dashboardapp.receipts.ReceiptViewer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SecondActivity extends AppCompatActivity {

    ReceiptManager receiptManager = ReceiptManager.getInstance();
    ReceiptViewer receiptViewer = ReceiptViewer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Load existing receipts and the screen
        String categoryToLoad = getReceiptViewer().getViewingCategory();
        TextView categoryTitle = findViewById(R.id.category_title);
        categoryTitle.setText(categoryToLoad);
        // Onclicks are added by the addReceipt function
        loadReceiptList(getReceiptManager().getReceiptsByCategory(categoryToLoad));
        addDeleteListeners();
    }

    /**
     * The onclick for the add button
     * Message box for the user to enter the title of the receipt and an ok/cancel button
     * @param view
     */
    public void addButtonOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
        View alertView = getLayoutInflater().inflate(R.layout.receipt_add_dialogue, null);
        // builder.setTitle("Add Receipt");
        builder.setView(alertView);

        EditText input = alertView.findViewWithTag("title_input");
        EditText priceInput = alertView.findViewWithTag("price_input");
        DatePicker datePicker = alertView.findViewWithTag("date_input");
        Calendar rightNow = Calendar.getInstance();
        int yeer = rightNow.get(Calendar.YEAR);
        int mont = rightNow.get(Calendar.MONTH);
        int dae = rightNow.get(Calendar.DAY_OF_MONTH);
        // Log.i("YEAR", Integer.toString(yeer));
        // Log.i("month", Integer.toString(mont));
        // Log.i("day", Integer.toString(dae));
        datePicker.updateDate(yeer, mont, dae);

        // set up the buttons
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (text.length() == 0){
                    Toast.makeText(SecondActivity.this,
                            "Enter some text!", Toast.LENGTH_SHORT).show();
                } else {
                    String category = getReceiptViewer().getViewingCategory();
                    String price = priceInput.getText().toString();
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth();
                    int day = datePicker.getDayOfMonth();
                    Receipt r = getReceiptManager().addReceipt(text, price, "", category,
                            year, month, day);
                    addReceipt(r);
                    addDeleteListeners();
                    // TODO make a drawable with rounded corners so it looks beautiful
                    // Log.i("bruh", Integer.toString(year) + Integer.toString(month) + Integer.toString(day));
                }
            }
        });

        builder.setNegativeButton("CANCEL", null);
        builder.show();

    }

    public void addDeleteListeners() {
        LinearLayout allReceiptsList = findViewById(R.id.all_receipts_list);
        int count = allReceiptsList.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = allReceiptsList.getChildAt(i);
            if (child.getId() == R.id.userCreated) {
                child.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        TextView title = child.findViewWithTag("text_3");
                        String rTitle = title.getText().toString();
                        String fTitle = rTitle.length() > 5 ? rTitle.substring(0, 4) + "..." :
                                rTitle;
                        // This should be the id
                        int id = Integer.parseInt(child.getTag().toString());
                        new AlertDialog.Builder(SecondActivity.this)
                                .setTitle("Delete Receipt")
                                .setMessage("Are you sure you wanna delete '"
                                        + fTitle + "'?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        visualDelete(id);
                                        getReceiptManager().deleteReceipt(id);
                                        Log.i("SecondActivity", Integer.toString(getReceiptManager().getReceiptList().size()));
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                        return true;
                    }
                });
            }
        }
    }

    /**
     * Adds a receipt to the display with onclicks and everything
     * @param receipt
     */
    private void addReceipt(Receipt receipt) {
        /* This method SHOULD NOT add a receipt to the backend.
        *  This method will also be used for the onLoad function later
        *  So it should only visually load the receipt onto the screen with onclicks
        * */
        int maxLength = 12;
        int maxPLength = 6;
        String titleToDisplay = "";
        String priceToDisplay = "";
        // Process the length of the title
        if (receipt.getTitle().length() > maxLength){
            titleToDisplay = receipt.getTitle().substring(0, maxLength) + "...";
        } else {
            titleToDisplay = receipt.getTitle();
        }
        if (receipt.getAmount().length() > maxPLength) {
            priceToDisplay = receipt.getAmount().substring(0, maxPLength) + "...";
        } else {
            priceToDisplay = receipt.getAmount();
        }

        // VISUALLY ADDS THE RECEIPT
        View receiptView = visualAddReceipt(titleToDisplay, priceToDisplay);
        receiptView.setTag(Integer.toString(receipt.getId()));
        receiptView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReceipt(receipt.getId());
            }
        });
    }

    /**
     * Handles the visuals of adding a receipt to the display. Also returns the view it generated.
     * Names it whatever you want to name it.
     * @param strIn: title of the receipt
     */
    private View visualAddReceipt(String strIn, String price) {
        LinearLayout receiptList = findViewById(R.id.all_receipts_list);
        View newCard = getLayoutInflater().inflate(R.layout.receipt_card, receiptList, false);
        // TODO maybe add animations
        TextView newCardNumber = newCard.findViewWithTag("text_2");
        TextView newCardText = newCard.findViewWithTag("text_3");
        TextView priceText = newCard.findViewWithTag("text_price");

        newCardNumber.setText("-");
        // TODO maybe number the tasks but like...yeah
        newCardText.setText(strIn);
        priceText.setText(price);
        receiptList.addView(newCard);
        return newCard;
    }

    private void visualDelete(int id) {
        LinearLayout allReceiptsList = findViewById(R.id.all_receipts_list);
        int count = allReceiptsList.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = allReceiptsList.getChildAt(i);
            if (child.getId() == R.id.userCreated) {
                if (Integer.parseInt(child.getTag().toString()) == id) {
                    allReceiptsList.removeView(child);
                    break;
                }
            }
        }
    }

    /**
     * Loads a list of receipts.
     * @param a
     */
    private void loadReceiptList(ArrayList<Receipt> a) {
        for (Receipt item: a) {
            addReceipt(item);
        }
    }

    public void goBack(View view) {
        finish();
    }

    // This whole updateReceipt and visualAddReceipt being literally 2 of the same function
    // Is kinda unfortunate...change this later
    private void updateReceipt(int id) {
        LinearLayout receiptList = findViewById(R.id.all_receipts_list);
        View receipt = receiptList.findViewWithTag(Integer.toString(id));
        if (receipt == null) {
            return;
        }
        TextView receiptText = receipt.findViewWithTag("text_3");
        TextView priceText = receipt.findViewWithTag("text_price");
        if (receiptText == null || priceText == null) {
            return;
        }
        Receipt r = receiptManager.getReceipt(id);
        int maxLength = 12;
        int maxPLength = 6;
        String titleToDisplay = "";
        String priceToDisplay = "";
        // Process the length of the title
        if (r.getTitle().length() > maxLength){
            titleToDisplay = r.getTitle().substring(0, maxLength) + "...";
        } else {
            titleToDisplay = r.getTitle();
        }
        if (r.getAmount().length() > maxPLength) {
            priceToDisplay = r.getAmount().substring(0, maxPLength) + "...";
        } else {
            priceToDisplay = r.getAmount();
        }
        receiptText.setText(titleToDisplay);
        priceText.setText(priceToDisplay);
    }

    // some get functions
    public ReceiptManager getReceiptManager() {
        return this.receiptManager;
    }
    public ReceiptViewer getReceiptViewer() {return this.receiptViewer;}

    public void openReceipt(int id) {
        getReceiptViewer().setViewingReceiptID(id);
        startActivityForResult(new Intent(SecondActivity.this, ThirdActivity.class), 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            updateReceipt(resultCode);
            Log.i("BRUH", "BRUH");
        }
    }
}