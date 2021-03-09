package com.example.dashboardapp.receipts;

import android.util.Log;

import java.util.ArrayList;

public class ReceiptManager {

    private static ReceiptManager receiptManager = null;
    private ArrayList<Receipt> receiptList;
    private ArrayList<String> categoryList;
    private int nextId;

    private ReceiptManager(){
        this.receiptList = new ArrayList<Receipt>();
        this.categoryList = new ArrayList<String>();
        this.nextId = 0;
    }

    private ReceiptManager(ArrayList<Receipt> a){
        this.receiptList = new ArrayList<Receipt>();
        this.categoryList = new ArrayList<String>();
        this.nextId = 0;
        for (Receipt item:a){
            this.addReceipt(item.getTitle(),
                    item.getAmount(),
                    item.getDescription(),
                    item.getCategory(),
                    item.getYear(),
                    item.getMonth(),
                    item.getDay());
        }
    }

    /**
     * Adds a receipt with an id, then increments the id.
     * @param title
     * @param amount
     * @param description
     * @param category
     */
    public Receipt addReceipt(String title, String amount, String description, String category,
                              int y, int m, int d){
        // TODO overload this function to not have to take the other parameters
        Receipt receiptIn = new Receipt(this.nextId, title, amount, description, category, y, m, d);
        this.receiptList.add(receiptIn);
        this.nextId++;

        // May be redundant
        this.tryAddCategory(receiptIn.getCategory());
        return receiptIn;
    }

    /**
     * Deletes the receipt by id. Uses linear search.
     * @param id
     */
    public void deleteReceipt(int id) {
        for (int i = 0; i < this.receiptList.size(); i++) {
            if (this.receiptList.get(i).getId() == id) {
                this.receiptList.remove(i);
                Log.i("ReceiptManager", "Deleted receipt at index: " + i);
                break;
            }
        }
    }

    /**
     * Looks for a receipt using id, CAN RETURN NULL!!
     * @param id
     * @return item
     */
    public Receipt getReceipt(int id) {
        for (Receipt item:this.receiptList) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public ArrayList<String> getCategoryList() {
        return this.categoryList;
    }

    public void tryAddCategory(String category) {
        if (!checkCategoryExists(category)){
            this.categoryList.add(category);
        }
    }

    public boolean checkCategoryExists(String category) {
        for (String item:this.categoryList){
            if (item.equals(category)){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of receipts that have a category
     * @param category
     * @return
     */
    public ArrayList<Receipt> getReceiptsByCategory(String category) {
        ArrayList<Receipt> return_list = new ArrayList<>();
        for (Receipt item: this.receiptList){
            if (item.getCategory().equals(category)){
                return_list.add(item);
            }
        }
        return return_list;
    }

    /**
     * Deletes a category from the category list,
     * then deletes all receipts from that category
     * @param category
     */
    public void deleteCategory(String category) {
        for (String item:this.categoryList){
            if (item.equals(category)){
                this.categoryList.remove(item);
                break;
            }
        }
        // This is inefficient, should search starting from last index
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (Receipt r: this.receiptList){
            if (r.getCategory() == category) {
                ids.add(r.getId());
            }
        }
        for (int i: ids) {
            deleteReceipt(i);
        }
    }

    public ArrayList<Receipt> getReceiptList() {
        return this.receiptList;
    }

    public static ReceiptManager getInstance() {
        if (receiptManager == null) {
            receiptManager = new ReceiptManager();
        }
        return receiptManager;
    }

}
