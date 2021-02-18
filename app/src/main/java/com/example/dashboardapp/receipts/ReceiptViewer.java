package com.example.dashboardapp.receipts;

public class ReceiptViewer {

    private static ReceiptViewer receiptViewer = null;
    private int viewingReceiptID;
    private String viewingCategory;

    public int getViewingReceiptID() {
        return this.viewingReceiptID;
    }

    public void setViewingReceiptID(int viewingReceiptID) {
        this.viewingReceiptID = viewingReceiptID;
    }

    public String getViewingCategory() {
        return this.viewingCategory;
    }

    public void setViewingCategory(String viewingCategory) {
        this.viewingCategory = viewingCategory;
    }

    public static ReceiptViewer getInstance() {
        if (receiptViewer == null){
            receiptViewer = new ReceiptViewer();
        }
        return receiptViewer;
    }
}
