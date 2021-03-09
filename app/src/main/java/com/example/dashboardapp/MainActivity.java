package com.example.dashboardapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dashboardapp.receipts.ReceiptManager;
import com.example.dashboardapp.receipts.ReceiptViewer;

public class MainActivity extends AppCompatActivity {

    private static ReceiptManager receiptManager = ReceiptManager.getInstance();
    private static ReceiptViewer receiptViewer = ReceiptViewer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO IMPLEMENT SQLITEDATABASE
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getReceiptManager().tryAddCategory("Business");
        getReceiptManager().tryAddCategory("Personal");

        GridLayout dashboard = findViewById(R.id.dashboard_grid);
        for (String i:getReceiptManager().getCategoryList()) {

            if (!i.equals("Business") && !i.equals("Personal")) {
                addTaskCard(dashboard, i);
            }
        }

        addOpenListeners();
        addDeleteListeners();

    }

    /**
     * Gives a dialog box prompting to enter a category, then adds the card
     * if the input is not blank.
     * @param view
     */
    public void askToAddTaskCard(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Category");

        EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // set up the buttons
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (text.length() == 0){
                    Toast.makeText(MainActivity.this,
                            "Enter some text!", Toast.LENGTH_SHORT).show();
                } else if (getReceiptManager().checkCategoryExists(text)) {
                    Toast.makeText(MainActivity.this, "Duplicate Category!", Toast.LENGTH_SHORT).show();
                } else {
                    addTaskCard(view, text);
                }
            }
        });

        builder.setNegativeButton("CANCEL", null);
        builder.show();

    }

    // Helper functions
    /**
     * Handles the actual adding of a task card to the screen
     * Also tries to add new category to the receipt manager
     * @param view
     * @param name
     */
    private void addTaskCard(View view, String name) {
        /*
            We used to check for duplicate category here, but moved it to the onClick thing
            Because of loading issues. Idk if that'll be good.
         */
        // Get the dashboard and current plus button
        GridLayout dashboard = findViewById(R.id.dashboard_grid);
        CardView plusButton = findViewById(R.id.addCardButton);

        // Delete the current plus button
        dashboard.removeView(plusButton);

        // Add the card and save it to a variable
        View newFolder = addCard(name, R.drawable.folder, true);

        // Try to add this newly created category
        this.getReceiptManager().tryAddCategory(name);
        Log.i("Categories", this.getReceiptManager().getCategoryList().toString());

        // Add a new plus button and set the id
        View newPlusButton = addCard("New Folder", R.drawable.plus, false);
        newPlusButton.setId(R.id.addCardButton);
        newPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askToAddTaskCard(v);
            }
        });
        addDeleteListeners();
        addOpenListeners();
    }

    /**
     * Adds a card to the dashboard and returns the card's view
     * @param strIn
     * @param imageID
     * @param shouldAnimate
     * @return
     */
    private View addCard(String strIn, int imageID, boolean shouldAnimate) {
        GridLayout dashboard = findViewById(R.id.dashboard_grid);
        View newCard = getLayoutInflater().inflate(R.layout.folder_card, dashboard, false);
        if (shouldAnimate) {
            cardAppear(newCard);
        }
        TextView newCardText = newCard.findViewWithTag("text_1");
        newCardText.setText(strIn);
        ImageView newCardImage = newCard.findViewWithTag("image");
        newCardImage.setImageResource(imageID);

        dashboard.addView(newCard);
        return newCard;
    }

    /**
     * Adds Delete listeners to every user-created card.
     */
    private void addDeleteListeners() {
        GridLayout dashboard = findViewById(R.id.dashboard_grid);
        int count = dashboard.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = dashboard.getChildAt(i);
            if (child.getId() == R.id.userCreated) {
                child.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        TextView title = child.findViewWithTag("text_1");
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Delete Category")
                                .setMessage("Are you sure you wanna delete "
                                        + title.getText().toString() + "?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
                                        anim.setDuration(200);
                                        child.startAnimation(anim);
                                        getReceiptManager().deleteCategory(title.getText().toString());
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dashboard.removeView(child);
                                            }
                                        }, 200);
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
     * Adds onClick that gets the category folder and yeah...
     */
    private void addOpenListeners() {
        GridLayout dashboard = findViewById(R.id.dashboard_grid);
        int count = dashboard.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = dashboard.getChildAt(i);
            if (child.getId() != R.id.addCardButton) {
                // Get the category of the child
                TextView category = child.findViewWithTag("text_1");
                String text = category.getText().toString();
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCategory(text);
                    }
                });
            }
        }
    }

    private void cardAppear(View view) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.animate().scaleX(1).setDuration(200);
        view.animate().scaleY(1).setDuration(200);
    }

    // Some get functions
    public ReceiptManager getReceiptManager() {
        return this.receiptManager;
    }
    public ReceiptViewer getReceiptViewer() {return this.receiptViewer;}

    public void openCategory(String category) {
        getReceiptViewer().setViewingCategory(category);
        startActivity(new Intent(MainActivity.this, SecondActivity.class));
    }

}