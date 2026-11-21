package com.jbm.mini;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.*;
import android.text.InputType;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private DB db;
    private LinearLayout root, rows;
    private EditText nameInput, qtyInput;
    private TextView totalText, emptyText;
    private final ArrayList<Integer> ids = new ArrayList<>();
    private int navy = Color.rgb(20,36,58), gold = Color.rgb(212,154,53);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DB(this);
        buildScreen();
        refresh();
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }
    private void buildScreen() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(246,247,249));
        setContentView(root);
        LinearLayout header = new LinearLayout(this); header.setOrientation(LinearLayout.VERTICAL); header.setPadding(dp(20),dp(22),dp(20),dp(20)); header.setBackgroundColor(navy);
        header.addView(text("JBM DOORS & INTERIOR",20,Color.WHITE,true));
        TextView sub = text("MINI  •  STOCK MANAGER",12,0xFFE7C98F,true); sub.setPadding(0,dp(6),0,0); header.addView(sub);
        root.addView(header);
        LinearLayout content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(16),dp(16),dp(16),dp(12));
        root.addView(content, new LinearLayout.LayoutParams(-1,-1));
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(16),dp(14),dp(16),dp(16)); card.setBackgroundColor(Color.WHITE);
        content.addView(card, new LinearLayout.LayoutParams(-1,-2));
        card.addView(text("Add Door Stock",18,navy,true));
        nameInput = new EditText(this); nameInput.setSingleLine(true); nameInput.setHint("Door name (e.g. Membrane  flush)"); nameInput.setTextSize(14); nameInput.setPadding(dp(10),0,dp(10),0);
        card.addView(nameInput, new LinearLayout.LayoutParams(-1,dp(48)));
        qtyInput = new EditText(this); qtyInput.setSingleLine(true); qtyInput.setInputType(InputType.TYPE_CLASS_NUMBER); qtyInput.setHint("Quantity (pieces)"); qtyInput.setTextSize(14); qtyInput.setPadding(dp(10),0,dp(10),0);
        LinearLayout.LayoutParams qp = new LinearLayout.LayoutParams(-1,dp(48)); qp.topMargin=dp(8); card.addView(qtyInput,qp);
        Button add = new Button(this); add.setText("ADD TO STOCK"); add.setTextColor(Color.WHITE); add.setBackgroundTintList(android.content.res.ColorStateList.valueOf(navy));
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1,dp(48)); ap.topMargin=dp(10); card.addView(add,ap); add.setOnClickListener(v -> addStock());
        LinearLayout summary = new LinearLayout(this); summary.setPadding(dp(14),dp(14),dp(14),dp(14)); summary.setGravity(Gravity.CENTER_VERTICAL); summary.setBackgroundColor(0xFFFFF7E8);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1,-2); sp.topMargin=dp(14); content.addView(summary,sp);
        summary.addView(text("TOTAL DOORS IN STOCK",12,navy,true), new LinearLayout.LayoutParams(0,-2,1));
        totalText = text("0",24,navy,true); summary.addView(totalText);
        TextView heading = text("Stock List",18,navy,true); heading.setPadding(dp(2),dp(18),0,dp(8)); content.addView(heading);
        ScrollView scroll = new ScrollView(this); content.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        rows = new LinearLayout(this); rows.setOrientation(LinearLayout.VERTICAL); scroll.addView(rows);
        emptyText = text("No stock added yet. Add your first door above.",14,0xFF777777,false); emptyText.setGravity(Gravity.CENTER); emptyText.setPadding(dp(10),dp(24),dp(10),dp(24)); rows.addView(emptyText);
        TextView foot = text("Tap an item to edit • Long-press to delete",11,0xFF777777,false); foot.setGravity(Gravity.CENTER); foot.setPadding(0,dp(8),0,0); content.addView(foot);
    }
    private void addStock() {
        String n=nameInput.getText().toString().trim(), q=qtyInput.getText().toString().trim();
        if(n.isEmpty() || q.isEmpty() || Integer.parseInt(q)<=0) { Toast.makeText(this,"Enter door name and a quantity greater than zero",Toast.LENGTH_SHORT).show(); return; }
        ContentValues cv=new ContentValues(); cv.put("name",n); cv.put("qty",Integer.parseInt(q)); db.getWritableDatabase().insert("stock",null,cv);
        nameInput.setText(""); qtyInput.setText(""); ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(qtyInput.getWindowToken(),0); refresh();
        Toast.makeText(this,"Stock added",Toast.LENGTH_SHORT).show();
    }
    private void refresh() {
        rows.removeAllViews(); ids.clear(); int total=0; Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,qty FROM stock ORDER BY name COLLATE NOCASE",null);
        while(c.moveToNext()) { int id=c.getInt(0), qty=c.getInt(2); String name=c.getString(1); ids.add(id); total+=qty;
            LinearLayout item=new LinearLayout(this); item.setOrientation(LinearLayout.HORIZONTAL); item.setGravity(Gravity.CENTER_VERTICAL); item.setPadding(dp(14),dp(12),dp(14),dp(12)); item.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,-2); ip.bottomMargin=dp(7); rows.addView(item,ip);
            LinearLayout info=new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL); item.addView(info,new LinearLayout.LayoutParams(0,-2,1)); info.addView(text(name,15,navy,true)); info.addView(text("Tap to edit  •  Hold to delete",10,0xFF888888,false));
            TextView count=text(qty+" pcs",16,gold,true); item.addView(count);
            item.setOnClickListener(v->editStock(id,name,qty)); item.setOnLongClickListener(v->{new AlertDialog.Builder(this).setTitle("Delete stock?").setMessage("Remove "+name+" from the list?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{db.getWritableDatabase().delete("stock","id=?",new String[]{String.valueOf(id)});refresh();}).show();return true;});
        }
        c.close(); totalText.setText(String.valueOf(total));
        if(ids.isEmpty()) rows.addView(emptyText); 
    }
    private void editStock(int id,String oldName,int oldQty) {
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(4),dp(4),dp(4),0);
        EditText n=new EditText(this); n.setSingleLine(true); n.setText(oldName); n.setHint("Door name"); box.addView(n);
        EditText q=new EditText(this); q.setSingleLine(true); q.setInputType(InputType.TYPE_CLASS_NUMBER); q.setText(String.valueOf(oldQty)); q.setHint("Quantity"); box.addView(q);
        new AlertDialog.Builder(this).setTitle("Edit Stock").setView(box).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{
            String name=n.getText().toString().trim(), qty=q.getText().toString().trim(); if(name.isEmpty()||qty.isEmpty()||Integer.parseInt(qty)<0){Toast.makeText(this,"Please enter valid details",Toast.LENGTH_SHORT).show();return;}
            ContentValues cv=new ContentValues();cv.put("name",name);cv.put("qty",Integer.parseInt(qty));db.getWritableDatabase().update("stock",cv,"id=?",new String[]{String.valueOf(id)});refresh();
        }).show();
    }
    static class DB extends SQLiteOpenHelper {
        DB(Context c){super(c,"jbm_stock.db",null,1);} public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE stock(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,qty INTEGER NOT NULL)");}
        public void onUpgrade(SQLiteDatabase d,int o,int n){}
    }
}
