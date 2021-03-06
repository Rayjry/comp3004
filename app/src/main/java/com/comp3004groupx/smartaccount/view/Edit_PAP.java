package com.comp3004groupx.smartaccount.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.comp3004groupx.smartaccount.Core.Account;
import com.comp3004groupx.smartaccount.Core.Date;
import com.comp3004groupx.smartaccount.Core.PAP;
import com.comp3004groupx.smartaccount.R;
import com.comp3004groupx.smartaccount.module.DAO.AccountDAO;
import com.comp3004groupx.smartaccount.module.DAO.PAPDAO;
import com.comp3004groupx.smartaccount.module.DAO.PurchaseTypeDAO;
import com.comp3004groupx.smartaccount.module.DecimalDigitsInputFilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alex Meng on 2017/11/16.
 */

public class Edit_PAP extends AppCompatActivity {

    PurchaseTypeDAO purchaseTypeList;
    AccountDAO accounts;
    PAPDAO papdao;
    PAP tran;
    Spinner yearSpinner;
    Spinner monthSpinner;
    Spinner daySpinner;
    Spinner periodSpinner;
    EditText amount;
    Spinner purchaseTypeSpinner;
    Spinner accountSpinner;
    EditText notes;
    Button saveButton;
    Button deleteButton;
    DecimalFormat decimalFormat;
    List<Integer> Day = new ArrayList<>();
    List<PAP> allpap = new ArrayList<>();
    int status = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_pap);
        decimalFormat = new DecimalFormat("0.00");
        //Get button
        saveButton = (Button) findViewById(R.id.save);
        deleteButton = (Button) findViewById(R.id.delete);

        //Get tran from last activity
        Intent intent = getIntent();
        int tranId = intent.getIntExtra("ID",0);
        papdao = new PAPDAO(getApplicationContext());
        allpap = papdao.getAutoDesc();
        for(PAP t : allpap){
            if(t.getId()==tranId)
                tran = t;
        }

        //Init Type Spinner
        purchaseTypeSpinner = (Spinner) findViewById(R.id.PAPpurchaseTypeSpinner);
        purchaseTypeList = new PurchaseTypeDAO(getApplicationContext());
            //Init purchaseTypeSpinner)
            List<String> purchaseTypes = purchaseTypeList.getALLExpenseType();
            setTypeSpinner(purchaseTypeSpinner, purchaseTypes);

        //Init DateSpinner
        yearSpinner = (Spinner) findViewById(R.id.PAPyearSpinner);
        monthSpinner = (Spinner) findViewById(R.id.PAPmonthSpinner);
        daySpinner = (Spinner) findViewById(R.id.PAPdaySpinner);
        setDateSpinner(yearSpinner, monthSpinner, daySpinner);

        //Init accountSpinner
        accountSpinner = (Spinner) findViewById(R.id.PAPaccountSpinner);
        accounts = new AccountDAO(getApplicationContext());
        ArrayList<Account> accountTypes = accounts.getAllAccount();
        List<String> accountsNames = new ArrayList<>();
        for (int i =0; i<accountTypes.size(); i++){
            accountsNames.add(accountTypes.get(i).getName());
        }
        setTypeSpinner(accountSpinner,accountsNames);

        periodSpinner = (Spinner)findViewById(R.id.period);
        List<String> periods = new ArrayList<>();
        periods.add("Select a Number");
        for(int i=1; i<=12;i++){
            periods.add(Integer.toString(i));
        }
        setTypeSpinner(periodSpinner,periods);

        //Set amount
        amount = (EditText) findViewById(R.id.PAPamount);
        amount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        if (status == 0){
            amount.setText(decimalFormat.format(tran.getAmount()));
        }
        else {
            amount.setText(decimalFormat.format(-1*tran.getAmount()));
        }

        //Set notes
        notes = (EditText) findViewById(R.id.PAPnotes);
            notes.setText(tran.getNote());

        //Set PurchaseTypeSpinner
        int purchaseTypePosition = getTypePosition(purchaseTypeSpinner, tran.getType());
        purchaseTypeSpinner.setSelection(purchaseTypePosition);

        //Set accountSpinner
        int accountPosition = getTypePosition(accountSpinner, tran.getAccount());
        accountSpinner.setSelection(accountPosition);

        //Set yearSpinner
        int yearPosition = getTypePosition(yearSpinner, Integer.toString(tran.getDate().getYear()));
        yearSpinner.setSelection(yearPosition);

        //Set monthSpinner
        int monthPosition = getTypePosition(monthSpinner, Integer.toString(tran.getDate().getMonth()));
        monthSpinner.setSelection(monthPosition);

        //Set daySpinner
        int dayPosition = getTypePosition(daySpinner,Integer.toString(tran.getDate().getDay()));
        daySpinner.setSelection(dayPosition);

        //Set period
        int periodPosition = getTypePosition(periodSpinner,Integer.toString(tran.getPERIOD()));
        periodSpinner.setSelection(periodPosition);

        //Update Transaction
        updatePAP();

        //Delete Transaction
        deletePAP();





    }

    private void setYearSpinner(Spinner YearSpinner){
        List<Integer> Year = new ArrayList<>();
        for (int i=2017;i<2049;i++){
            Year.add(i);
        }
        ArrayAdapter<Integer> YearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Year);
        YearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YearSpinner.setAdapter(YearAdapter);
    }
    private void setMonthSpinner(Spinner MonthSpinner){
        List<Integer> Month = new ArrayList<>();
        for (int i=1;i<=12;i++){
            Month.add(i);
        }
        ArrayAdapter<Integer> MonthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Month);
        MonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MonthSpinner.setAdapter(MonthAdapter);
    }
    private void setDayspinner(Spinner MonthSpinner, final Spinner DaySpinner) {
        int Month = Integer.parseInt(MonthSpinner.getSelectedItem().toString());
        if (Month == 1 || Month == 3 || Month == 5 || Month == 7 || Month == 8 || Month == 10 || Month == 12) {
            for (int i = 1; i <= 31; i++) {
                Day.add(i);
            }
        }
        else if (Month == 2){
            for (int i=1;i<=28;i++){
                Day.add(i);
            }
        }
        else{
            for (int i=1;i<=30;i++){
                Day.add(i);
            }
        }
        final ArrayAdapter<Integer> DayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Day);
        DayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DaySpinner.setAdapter(DayAdapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Day.clear();
                String selectedItem = monthSpinner.getSelectedItem().toString();
                int Month = Integer.parseInt(selectedItem);
                if (Month == 1 || Month == 3 || Month == 5 || Month == 7 || Month == 8 || Month == 10 || Month == 12) {
                    for (int i = 1; i <= 31; i++) {
                        Day.add(i);
                    }
                }
                else if (Month == 2){
                    for (int i=1;i<=28;i++){
                        Day.add(i);
                    }
                    DayAdapter.remove(31);
                    DayAdapter.remove(30);
                    DayAdapter.remove(29);
                }
                else{
                    for (int i=1;i<=30;i++){
                        Day.add(i);
                    }
                    DayAdapter.remove(31);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    private void setDateSpinner(Spinner yearSpinner, Spinner monthSpinner, Spinner daySpinner){
        setYearSpinner(yearSpinner);
        setMonthSpinner(monthSpinner);
        setDayspinner(monthSpinner,daySpinner);
    }
    private void setTypeSpinner(Spinner TypeSpinner, List<String> Types){
        List<String> TypeSpinnerList = new ArrayList<>();
        for (int i = 0;i<Types.size(); i++){
            TypeSpinnerList.add(Types.get(i));
        }
        ArrayAdapter<String> typeDateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TypeSpinnerList);
        typeDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TypeSpinner.setAdapter(typeDateAdapter);
    }
    private int getTypePosition (Spinner TypeSpinner, String toComp){
        for (int i = 0; i<TypeSpinner.getAdapter().getCount(); i++){
            String checkS=TypeSpinner.getAdapter().getItem(i).toString();
            if(toComp.equals(TypeSpinner.getAdapter().getItem(i).toString())){
                return i;
            }
        }
        return 0;
    }

    private void updatePAP(){
        yearSpinner = (Spinner) findViewById(R.id.PAPyearSpinner);
        amount = (EditText) findViewById(R.id.PAPamount);
        purchaseTypeSpinner = (Spinner) findViewById(R.id.PAPpurchaseTypeSpinner);
        accountSpinner = (Spinner) findViewById(R.id.PAPaccountSpinner);
        monthSpinner = (Spinner) findViewById(R.id.PAPmonthSpinner);
        daySpinner = (Spinner) findViewById(R.id.PAPdaySpinner);
        notes = (EditText) findViewById(R.id.PAPnotes);
        saveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Date upDate = getDate(yearSpinner,monthSpinner,daySpinner);
                double upAmount = Double.parseDouble(amount.getText().toString());
                String upPurchaseType = purchaseTypeSpinner.getSelectedItem().toString();
                String upAccountName = accountSpinner.getSelectedItem().toString();
                String upNotes = notes.getText().toString();
                int upPeriod = Integer.parseInt(periodSpinner.getSelectedItem().toString());
                double difference = tran.getAmount()-upAmount;
                if (errorChecking(upAmount) && checkingDate(upDate)){
                    tran.setAccount(upAccountName);
                    tran.setAmount(upAmount);
                    tran.setDate(upDate);
                    tran.setType(upPurchaseType);
                    tran.setNote(upNotes);
                    tran.setPERIOD(upPeriod);
                    if (papdao.modifyAutoDesc(tran) && modifyRealBalance(tran, difference)){
                            toast("Success");
                            finish();
                    }

                }
            }
        });
    }
    private void deletePAP(){
        deleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(papdao.removeAutoDesc(tran.getId())&&modifyRealBalance(tran, tran.getAmount())){
                    toast("Success");
                    finish();
                }
            }
        });
    }

    private boolean checkingDate(Date date){
        Calendar calendar = Calendar.getInstance(Locale.CANADA);
        Date now = new Date(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
        if(date.compareTo(now)!=1) {
            toast("Please check date");
            return false;
        }
        return true;
    }
    private boolean errorChecking (double upAmount){
        boolean noError = true;
        if (upAmount == 0){
            noError = false;
            toast("New Amount can't be 0");
        }
        return noError;
    }
    private Date getDate(Spinner yearSpinner, Spinner monthSpinner, Spinner daySpinner){
        int year, month, day;
        year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
        month = Integer.parseInt(monthSpinner.getSelectedItem().toString());
        day = Integer.parseInt(daySpinner.getSelectedItem().toString());
        return new Date(year, month, day);
    }
    private void toast(String text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    private boolean modifyRealBalance(PAP tran, double amount){
        ArrayList<Account> allAccounts = accounts.getAllAccount();
        Account modify = null;

        for(Account a : allAccounts){
            if(a.getName().equals(tran.getAccount())) {
                modify = a;
                break;
            }
        }
        modify.setReal_balance(modify.getRealAmount()+amount);
        return accounts.updateAccount(modify);
    }

}
