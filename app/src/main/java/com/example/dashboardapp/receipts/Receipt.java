package com.example.dashboardapp.receipts;

public class Receipt {

    private final int id;
    private String title;
    private String amount;
    private String description;
    private String category;
    private int year;
    private int month;
    private int day;

    /**
     * Makes a new receipt with the params. For now we'll let the manager handle ids.
     * @param title
     * @param amount
     * @param description
     * @param category
     * @param id
     */
    public Receipt(int id, String title, String amount, String description, String category,
                   int year, int month, int day) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getId() {
        return this.id;
    }

    public String getCategory() {return this.category;}

}
