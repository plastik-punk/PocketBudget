package com.example.pocketbudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "pocket_budget";
    private static final int DB_VERSION = 2;
    private static final String TABLE_EXPENSES = "expenses";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COL_ID = "id";
    public static final String COL_DATE = "date";
    public static final String COL_AMOUNT = "amount";
    private static final String COL_CATEGORY_ID = "category_id";
    public static final String COL_CATEGORY_NAME = "category_name";
    private static final String COL_CATEGORY_PARENT_ID = "category_parent_id";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void addExpense(String category, double amount, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int id = -1;
        if (category != null && !category.isEmpty()) {
            id = getCategoryId(category);
            if (id == -1) {
                addCategory(category, null);
                id = getCategoryId(category);
            }
        }
        if (id == -1) {
            id = getCategoryId("uncategorized");
            if (id == -1) {
                addCategory("uncategorized", null);
                id = getCategoryId("uncategorized");
            }
        }
        values.put(COL_CATEGORY_ID, id);
        values.put(COL_AMOUNT, amount);
        values.put(COL_DATE, date.toString());

        db.insert(TABLE_EXPENSES, null, values);
    }

    public Cursor getExpensesForMonth(int year, int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_DATE, COL_AMOUNT, COL_CATEGORY_NAME};
        String selection = "strftime('%Y-%m', " + COL_DATE + ") = ?";
        String[] selectionArgs = {String.format(Locale.US, "%04d-%02d", year, month)};
        String orderBy = COL_DATE + " ASC";

        Cursor cursor = db.query(TABLE_EXPENSES + " INNER JOIN "
                + TABLE_CATEGORIES + " ON "
                + TABLE_EXPENSES + "."
                + COL_CATEGORY_ID + " = "
                + TABLE_CATEGORIES + "."
                + COL_ID, columns, selection, selectionArgs, null, null, orderBy);

        return cursor;
    }

    public void addCategory(String name, String parent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_CATEGORY_NAME, name);

        int id = -1;

        if (parent != null && !parent.isEmpty()) {
            id = getCategoryId(parent);
            if (id == -1) {
                addCategory(parent, null);
                id = getCategoryId(parent);
            }
        }
        values.put(COL_CATEGORY_PARENT_ID, id);

        db.insert(TABLE_CATEGORIES, null, values);
    }

    public int getCategoryId(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_ID + " FROM " + TABLE_CATEGORIES + " WHERE " + COL_CATEGORY_NAME + " = '" + name + "'";
        Cursor cursor = db.rawQuery(query, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createExpenseTable = "CREATE TABLE " + TABLE_EXPENSES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DATE + " DATE, "
                + COL_AMOUNT + " REAL, "
                + COL_CATEGORY_ID + " INTEGER, "
                + "FOREIGN KEY (" + COL_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COL_ID + ")"
                + ")";

        String createCategoryTable = "CREATE TABLE " + TABLE_CATEGORIES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CATEGORY_NAME + " TEXT, "
                + COL_CATEGORY_PARENT_ID + " INTEGER, "
                + "FOREIGN KEY (" + COL_CATEGORY_PARENT_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COL_ID + ")"
                + ")";

        sqLiteDatabase.execSQL(createCategoryTable);
        sqLiteDatabase.execSQL(createExpenseTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(sqLiteDatabase);
    }
}
