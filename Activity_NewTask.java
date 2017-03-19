package com.fispl.update_nfc_citizen_new;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fispl.current_loc.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Activity_NewTask extends Activity implements View.OnClickListener,View.OnFocusChangeListener
{
    RadioGroup rbg_selection;
    TextView lbl_lati , lbl_longi , lbl_total_square , lbl_m3;
    Button btn_openMap , btn_send_otp , btn_verify_otp , btn_finish;
    Spinner spn_off_rec , spn_district , spn_taluka , spn_village , spn_main_dept , spn_sub_dept;
    EditText txt_first_floor , txt_second_floor , txt_thired_floor , txt_mobile_number , txt_otp;
    EditText txt_send_number;
    TextView lbl_first_m3 , lbl_second_m3 , lbl_thired_m3;
    Button btn_start_date , btn_end_date;
    Calendar myCalendar = Calendar.getInstance();

    boolean used_aadhaar = true;
    String mUrl;
    String OTP;
    int first_floor = 0;
    int second_floor = 0;
    int thired_floor = 0;
    boolean with_aadhaar = false;

    float first_floor_m3 = 0.0f;
    float second_floor_m3 = 0.0f;
    float thired_floor_m3 = 0.0f;

    float map_lati = 0.0f;
    float map_longi = 0.0f;
    float total_m3 = 0.0f;
    float total_sqft = 0.0f;
    String map_address = "";
    String aadhaar_no = "", aadhaar_name="" , aadhaar_address = "" , aadhaar_gender = "" , aadhaar_mail = "";

    //Validations
    String Type = "Site";
    boolean map_ok = false;
    boolean has_generated = false;
    boolean is_valied_otp = false;

    public static final int SEND_SMS_PERMISSION = 103;
    String sms_number , sms_message;

    TelephonyManager telman;
    String[] villageId ;
    String selectedVillage = "";

    ArrayList<HashMap<String , String>> ListOfficer = new ArrayList<>();
    String[] mOfficer;
    ArrayList<HashMap<String , String>> ListDistrict = new ArrayList<>();
    String[] mDistrict;
    ArrayList<HashMap<String , String>> ListTaluka = new ArrayList<>();
    String[] mTaluka;
    ArrayList<HashMap<String , String>> ListVillage = new ArrayList<>();
    String[] mVillage;
    ArrayList<HashMap<String , String>> ListManDept = new ArrayList<>();
    String[] mMainDept;
    ArrayList<HashMap<String , String>> ListSubDept = new ArrayList<>();
    String[] mSubDept;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_task);

        telman = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        try
        {
            mUrl = getIntent().getExtras().getString("url");
            used_aadhaar = getIntent().getExtras().getBoolean("used_aadhaar");
            aadhaar_no = getIntent().getExtras().getString("aadhaar_no");
            aadhaar_name = getIntent().getExtras().getString("aadhaar_name");
            aadhaar_address = getIntent().getExtras().getString("aadhaar_address");
            aadhaar_gender = getIntent().getExtras().getString("aadhaar_gender");
            aadhaar_mail = getIntent().getExtras().getString("aadhaar_mail");

            total_sqft = getIntent().getExtras().getInt("total_sq_feet");
            total_m3 = getIntent().getExtras().getInt("total_sand_m3");
        }
        catch (Exception e)
        {
            Log.e("Error",e.toString());
        }

        rbg_selection = (RadioGroup) findViewById(R.id.rbg_site_resi);
        lbl_lati = (TextView) findViewById(R.id.lbl_lati);
        lbl_longi = (TextView) findViewById(R.id.lbl_longi);
        btn_openMap = (Button) findViewById(R.id.btn_openMap);
        spn_off_rec = (Spinner) findViewById(R.id.spn_off_rec);
        spn_district = (Spinner) findViewById(R.id.spn_district);
        spn_taluka = (Spinner) findViewById(R.id.spn_taluka);
        spn_village = (Spinner) findViewById(R.id.spn_village);
        txt_first_floor = (EditText) findViewById(R.id.txt_first_floor);
        txt_second_floor = (EditText) findViewById(R.id.txt_second_floor);
        txt_thired_floor = (EditText) findViewById(R.id.txt_thired_floor);
        txt_send_number= (EditText) findViewById(R.id.txt_send_number);
        lbl_total_square = (TextView) findViewById(R.id.lbl_total_square_feet);
        lbl_m3 = (TextView) findViewById(R.id.lbl_total_m3_sand);
        txt_mobile_number = (EditText) findViewById(R.id.txt_send_number);
        btn_send_otp = (Button) findViewById(R.id.btn_send_otp);
        txt_otp = (EditText) findViewById(R.id.txt_otp);
        btn_verify_otp = (Button) findViewById(R.id.btn_verify_otp);
        spn_main_dept = (Spinner) findViewById(R.id.spn_main_dept);
        spn_sub_dept = (Spinner) findViewById(R.id.spn_sub_dept);
        lbl_first_m3 = (TextView) findViewById(R.id.lbl_first_floor_m3);
        lbl_second_m3 = (TextView) findViewById(R.id.lbl_second_floor_m3);
        lbl_thired_m3 = (TextView) findViewById(R.id.lbl_thired_floor_m3);
        btn_start_date = (Button) findViewById(R.id.txt_start_date);
        btn_end_date = (Button) findViewById(R.id.txt_end_date);
        btn_finish = (Button) findViewById(R.id.btn_finish);

        btn_openMap.setOnClickListener(this);
        btn_send_otp.setOnClickListener(this);
        btn_verify_otp.setOnClickListener(this);
        btn_finish.setOnClickListener(this);

        Log.e("TEST",total_sqft + " | "+total_m3);
        if (!used_aadhaar)
        {
            lbl_total_square.setText(String.valueOf(total_sqft));
            lbl_m3.setText(String.valueOf(total_m3));
            txt_first_floor.setText(String.valueOf((int)total_sqft));
            lbl_first_m3.setText(lbl_m3.getText().toString());
            txt_first_floor.setEnabled(false);
            txt_second_floor.setEnabled(false);
            txt_thired_floor.setEnabled(false);
        }
        else
        {
            txt_first_floor.setOnFocusChangeListener(this);
            txt_second_floor.setOnFocusChangeListener(this);
            txt_thired_floor.setOnFocusChangeListener(this);
            txt_first_floor.setEnabled(true);
            txt_second_floor.setEnabled(true);
            txt_thired_floor.setEnabled(true);
        }

        if (!used_aadhaar)
        {
            btn_start_date.setEnabled(false);
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            btn_start_date.setText(sdf.format(Calendar.getInstance().getTime()));

            myCalendar.add(Calendar.MONTH, 6);
            btn_end_date.setText(sdf.format(myCalendar.getTime()));
        }
        else
            btn_start_date.setEnabled(true);

        rbg_selection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                Type = radioButton.getText().toString();
            }
        });
        // 2017 RRao

        if (SavedData.administrator == 1 && SavedData.super_master_new == 1)// enable scanning
        {
            lbl_lati.setEnabled(false);
            lbl_lati.setText("");
            lbl_longi.setEnabled(false);
            lbl_longi.setText("");
            spn_off_rec.setEnabled(false);
            //spn_off_rec.settext("");
            spn_district.setEnabled(false);
            //spn_district.settext("");
            spn_taluka.setEnabled(false);
            //spn_taluka.settext("");
            spn_village.setEnabled(false);
            //spn_village.settext("");
            txt_first_floor.setEnabled(false);
            txt_first_floor.setText("");
            txt_second_floor.setEnabled(false);
            txt_second_floor.setText("");
            txt_thired_floor.setEnabled(false);
            txt_thired_floor.setText("");
            txt_send_number.setEnabled(false);
            txt_send_number.setText("");
            spn_main_dept.setEnabled(false);
            //spn_main_dept.settext("");
            spn_sub_dept.setEnabled(false);
            //spn_sub_dept.settext("");
            btn_start_date.setEnabled(false);
            btn_start_date.setText("");
            btn_end_date.setEnabled(false);
            btn_end_date.setText("");
            // send OTP and just validate it and finish (click event)
        }
        else
        {

        }

        spn_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    //spn_taluka.setAdapter(null);
                    new process_get_taluka(position).execute();
                    mVillage= new String[1];
                    HashMap<String , String> mItem = new HashMap<>();
                    mItem.put("vill_id","0");
                    mItem.put("vill_name","- Select Village -");
                    mItem.put("taluka_id","0");
                    mVillage[0] = "- Select Village -";
                    ListVillage.add(mItem);
                    ArrayAdapter<String> adpVillage = new ArrayAdapter<String>(Activity_NewTask.this,
                            android.R.layout.simple_spinner_item,mVillage );
                    spn_village.setAdapter(adpVillage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        spn_village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    selectedVillage = villageId[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        spn_taluka.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                {
                    //spn_village.setAdapter(null);
                    new process_get_village(position).execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_main_dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                {
                    //spn_sub_dept.setAdapter(null);
                    new process_get_sub_dept(position).execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Activity_NewTask.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        new process_get_officer().execute();
        new process_get_district().execute();
        new process_get_main_dept().execute();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Update_date();
        }
    };
    private void Update_date() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        btn_start_date.setText(sdf.format(myCalendar.getTime()));

        myCalendar.add(Calendar.YEAR, 2);
        btn_end_date.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_openMap:
                btn_openMap.setEnabled(false);
                startActivityForResult(new Intent(Activity_NewTask.this,Activity_Map.class),101);
                break;
            case R.id.btn_send_otp:
                if (txt_mobile_number.getText().toString().trim().length() == 10)
                {
                    OTP = genearte_otp(6);
                    String msg = "Your OTP is : "+OTP;
                    sms_number = txt_mobile_number.getText().toString().trim();
                    sms_message = msg;
                    sendSMS(txt_mobile_number.getText().toString().trim(),msg);
                    has_generated = true;
                }
                else
                    Toast.makeText(this, "Enter your Mobile number", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_verify_otp:
                if (has_generated)
                {
                    if (txt_otp.getText().toString().trim().length() == 6)
                    {
                        if (txt_otp.getText().toString().equalsIgnoreCase(OTP))
                        {
                            is_valied_otp = true;
                            txt_otp.setTextColor(Color.GREEN);
                        }
                    }
                    else
                    {
                        txt_otp.setTextColor(Color.RED);
                        Toast.makeText(this, "Entered Wrong OTP", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(this, "First request your OTP", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_finish:
                if (validate_all_data())
                {
                    Intent iResult = new Intent(Activity_NewTask.this,Activity_New_register.class);
                    iResult.putExtra("allowed", true);
                    iResult.putExtra("type", Type);
                    iResult.putExtra("latitude", String.valueOf(map_lati));
                    iResult.putExtra("longitude", String.valueOf(map_longi));
                    iResult.putExtra("address", map_address );
                    iResult.putExtra("officer_name", ListOfficer.get(spn_off_rec.getSelectedItemPosition()).get("officer_name"));
                    iResult.putExtra("district_id", ListDistrict.get(spn_district.getSelectedItemPosition()).get("city_id"));
                    iResult.putExtra("taluka_id", ListTaluka.get(spn_taluka.getSelectedItemPosition()).get("taluka_id"));
                    iResult.putExtra("village_id", selectedVillage);//ListVillage.get(spn_village.getSelectedItemPosition()).get("vill_id"));
                    iResult.putExtra("tot_sq_ft", String.valueOf(total_sqft));
                    iResult.putExtra("tot_m3", String.valueOf(total_m3));
                    iResult.putExtra("sq_gf", txt_first_floor.getText().toString());
                    iResult.putExtra("cc_gf", lbl_first_m3.getText().toString());
                    iResult.putExtra("sq_ff", txt_second_floor.getText().toString());
                    iResult.putExtra("cc_ff", lbl_second_m3.getText().toString());
                    iResult.putExtra("sq_sf", txt_thired_floor.getText().toString());
                    iResult.putExtra("cc_sf", lbl_thired_m3.getText().toString());
                    iResult.putExtra("mobile_no", txt_mobile_number.getText().toString());
                    iResult.putExtra("dept_code", ListManDept.get(spn_main_dept.getSelectedItemPosition()).get("cust_type_code"));
                    iResult.putExtra("sub_dept_code",ListSubDept.get(spn_sub_dept.getSelectedItemPosition()).get("dept_id") );
                    iResult.putExtra("start_date", btn_start_date.getText().toString());
                    iResult.putExtra("end_date", btn_end_date.getText().toString());
                    iResult.putExtra("aadhaar_no", aadhaar_no);
                    iResult.putExtra("aadhaar_name", aadhaar_name);
                    iResult.putExtra("aadhaar_address", aadhaar_address);
                    iResult.putExtra("aadhaar_gender", aadhaar_gender);
                    setResult(Activity.RESULT_OK,iResult);
                    finish();
                }
                break;
        }
    }
    public boolean validate_all_data()
    {
        if (!map_ok)
        {
            Toast.makeText(this, "Select location first", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (spn_off_rec.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Select Officer", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (spn_district.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Select District", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (spn_taluka.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Select Taluka", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (spn_village.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Select Village", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (total_sqft == 0.0f || total_m3 == 0.0f)
        {
            Toast.makeText(this, "Enter Square feet", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!has_generated || !is_valied_otp)
        {
            Toast.makeText(this, "You have not verified your OTP", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (spn_main_dept.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Select Main Department", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (spn_sub_dept.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Select Sub Department", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (btn_start_date.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Select starting date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void sendSMS(String phoneNo, String msg){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                int per_sms = checkSelfPermission(Manifest.permission.SEND_SMS);
                if (per_sms != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION);
                else
                    send_sms();
            }
            else
                send_sms();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SEND_SMS_PERMISSION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                send_sms();
            else
                Toast.makeText(this, "You have to grant permission for sending SMS", Toast.LENGTH_SHORT).show();
        }
    }

    public void send_sms()
    {
        SmsManager smsManager = SmsManager.getDefault();
        //Toast.makeText(this, "OTP : "+sms_message, Toast.LENGTH_SHORT).show();
        smsManager.sendTextMessage(sms_number, null, sms_message, null, null);
        Toast.makeText(getApplicationContext(), "Check your SMS to get OTP.", Toast.LENGTH_LONG).show();
    }
    public String genearte_otp(int digits)
    {
        int max = (int) Math.pow(10,(digits)) - 1; //for digits =7, max will be 9999999
        int min = (int) Math.pow(10, digits-1); //for digits = 7, min will be 1000000
        int range = max-min; //This is 8999999
        Random r = new Random();
        int x = r.nextInt(range);// This will generate random integers in range 0 - 8999999
        int nDigitRandomNo = x+min; //Our random rumber will be any random number x + min
        return String.valueOf(nDigitRandomNo);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        btn_openMap.setEnabled(true);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK)
        {
            try
            {
                map_lati = (float) data.getExtras().getDouble("latitude");
                map_longi = (float) data.getExtras().getDouble("longitude");
                map_address = data.getExtras().getString("address");
                lbl_lati.setText(String.valueOf(map_lati));
                lbl_longi.setText(String.valueOf(map_longi));
                if (map_lati == 0.0 || map_longi == 0.0)
                    map_ok = false;
                else if (map_lati > 1.0 && map_longi > 1.0)
                    map_ok = true;
            }
            catch (Exception e)
            {
                map_ok = false;
                Log.e("Error",e.toString());
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        switch (v.getId())
        {
            case R.id.txt_first_floor:
                if (!hasFocus)
                {
                    if (txt_first_floor.getText().toString().trim().length() != 0)
                    {
                        if (Integer.valueOf(txt_first_floor.getText().toString().trim())> 0)
                        {
                            first_floor = Integer.parseInt(txt_first_floor.getText().toString());
                            first_floor_m3 = first_floor * 0.1f;
                            count_all();
                        }
                    }
                    else
                    {
                        first_floor = 0;
                        first_floor_m3 = 0.0f;
                    }
                }
                break;
            case R.id.txt_second_floor:
                if (!hasFocus)
                {
                    if (txt_second_floor.getText().toString().trim().length() != 0)
                    {
                        if (Integer.valueOf(txt_second_floor.getText().toString().trim())> 0)
                        {
                            second_floor = Integer.parseInt(txt_second_floor.getText().toString());
                            second_floor_m3 = second_floor * 0.075f;
                            count_all();
                        }
                    }
                    else
                    {
                        second_floor = 0;
                        second_floor_m3 = 0.0f;
                    }
                }
                break;
            case R.id.txt_thired_floor:
                if (!hasFocus)
                {
                    if (txt_thired_floor.getText().toString().trim().length() != 0)
                    {
                        if (Integer.valueOf(txt_thired_floor.getText().toString().trim())> 0)
                        {
                            thired_floor = Integer.parseInt(txt_thired_floor.getText().toString());
                            thired_floor_m3 = thired_floor * 0.075f;
                            count_all();
                        }
                    }
                    else
                    {
                        thired_floor = 0;
                        thired_floor_m3 = 0.0f;
                    }
                }
                break;
        }
    }
    public void count_all()
    {
        lbl_first_m3.setText(String.valueOf(first_floor_m3));
        lbl_second_m3.setText(String.valueOf(second_floor_m3));
        lbl_thired_m3.setText(String.valueOf(thired_floor_m3));

        total_m3 = first_floor_m3 + second_floor_m3 + thired_floor_m3;
        total_sqft = first_floor + second_floor + thired_floor;

        lbl_total_square.setText(String.valueOf(total_sqft));
        lbl_m3.setText(String.valueOf(total_m3));
    }

    public class process_get_officer extends AsyncTask<Void , Void , JSONObject>
    {
        Uri.Builder queryBuilder = new Uri.Builder();
        Https http = new Https(Activity_NewTask.this);
        String params;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            queryBuilder.appendQueryParameter("id", "43");
            queryBuilder.appendQueryParameter("value", telman.getDeviceId());
            queryBuilder.appendQueryParameter("app_name", "hsn_sand_stock");
            queryBuilder.appendQueryParameter("ver_3",String.valueOf(Utils.VERSION) );
            params = queryBuilder.build().getEncodedQuery();
        }

        @Override
        protected JSONObject doInBackground(Void... args0)
        {
            try {
                return http.POST(mUrl,params);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject !=null)
            {
                try
                {
                    if (jsonObject.getJSONArray("arr_1").length() > 0)
                    {
                        mOfficer = new String[jsonObject.getJSONArray("arr_1").length()+1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("officer_name","- Select Officer -");
                        mItem.put("officer_dept","");
                        mItem.put("officer_post","");
                        mItem.put("off_ref_id","0");
                        mOfficer[0] = "- Select Officer -";
                        ListOfficer.add(mItem);
                        for (int i=0;i<jsonObject.getJSONArray("arr_1").length();i++)
                        {
                            mItem = new HashMap<>();
                            mItem.put("officer_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("officer_name"));
                            mItem.put("officer_dept",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("officer_dept"));
                            mItem.put("officer_post",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("officer_post"));
                            mItem.put("off_ref_id",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("off_ref_id"));
                            mOfficer[i+1] = jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("officer_name");
                            ListOfficer.add(mItem);
                        }
                        ArrayAdapter<String> adpOfficer = new ArrayAdapter<String>(Activity_NewTask.this,android.R.layout.simple_spinner_item,mOfficer);
                        spn_off_rec.setAdapter(adpOfficer);
                    }
                    else
                    {
                        mOfficer = new String[1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("officer_name","- Select Officer -");
                        mItem.put("officer_dept","");
                        mItem.put("officer_post","");
                        mItem.put("off_ref_id","0");
                        mOfficer[0] = "- Select Officer -";
                        ListOfficer.add(mItem);
                        ArrayAdapter<String> adpOfficer = new ArrayAdapter<String>(Activity_NewTask.this,android.R.layout.simple_spinner_item,mOfficer);
                        spn_off_rec.setAdapter(adpOfficer);
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error",e.toString());
                    mOfficer = new String[1];
                    HashMap<String , String> mItem = new HashMap<>();
                    mItem.put("officer_name","- Select Officer -");
                    mItem.put("officer_dept","");
                    mItem.put("officer_post","");
                    mItem.put("off_ref_id","0");
                    mOfficer[0] = "- Select Officer -";
                    ListOfficer.add(mItem);
                    ArrayAdapter<String> adpOfficer = new ArrayAdapter<String>(Activity_NewTask.this,android.R.layout.simple_spinner_item,mOfficer);
                    spn_off_rec.setAdapter(adpOfficer);
                }
            }
            else
                Toast.makeText(Activity_NewTask.this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }
    public class process_get_district extends AsyncTask<Void , Void , JSONObject>
    {
        Uri.Builder queryBuilder = new Uri.Builder();
        Https http = new Https(Activity_NewTask.this);
        String params;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            queryBuilder.appendQueryParameter("id", "38");
            queryBuilder.appendQueryParameter("value", telman.getDeviceId());
            queryBuilder.appendQueryParameter("app_name", "hsn_sand_stock");
            queryBuilder.appendQueryParameter("ver_3",String.valueOf(Utils.VERSION) );
            params = queryBuilder.build().getEncodedQuery();
        }

        @Override
        protected JSONObject doInBackground(Void... args0)
        {
            try {
                return http.POST(mUrl,params);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject !=null)
            {
                try
                {
                    if (jsonObject.getJSONArray("arr_1").length() > 0)
                    {
                        mDistrict = new String[jsonObject.getJSONArray("arr_1").length()+1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("city_id","0");
                        mItem.put("city_name","- Select District -");
                        mItem.put("short_code","0");
                        mDistrict[0] = "- Select District -";
                        ListDistrict.add(mItem);
                        for (int i=0;i<jsonObject.getJSONArray("arr_1").length();i++)
                        {
                            mItem = new HashMap<>();
                            mItem.put("city_id",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("city_id"));
                            mItem.put("city_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("city_name"));
                            mItem.put("short_code",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("short_code"));
                            mDistrict[i+1] = jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("city_name");
                            ListDistrict.add(mItem);
                        }
                        ArrayAdapter<String> adpDistrict = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mDistrict );
                        spn_district.setAdapter(adpDistrict);
                    }
                    else
                    {
                        mDistrict = new String[1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("city_id","0");
                        mItem.put("city_name","- Select District -");
                        mItem.put("short_code","0");
                        mDistrict[0] = "- Select District -";
                        ListDistrict.add(mItem);
                        ArrayAdapter<String> adpDistrict = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mDistrict );
                        spn_district.setAdapter(adpDistrict);
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error",e.toString());
                    mDistrict = new String[1];
                    HashMap<String , String> mItem = new HashMap<>();
                    mItem.put("city_id","0");
                    mItem.put("city_name","- Select District -");
                    mItem.put("short_code","0");
                    mDistrict[0] = "- Select District -";
                    ListDistrict.add(mItem);
                    ArrayAdapter<String> adpDistrict = new ArrayAdapter<String>(Activity_NewTask.this,
                            android.R.layout.simple_spinner_item,mDistrict );
                    spn_district.setAdapter(adpDistrict);
                }
            }
            else
                Toast.makeText(Activity_NewTask.this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }
    public class process_get_taluka extends AsyncTask<Void , Void , JSONObject>
    {
        Uri.Builder queryBuilder = new Uri.Builder();
        Https http = new Https(Activity_NewTask.this);
        String params;
        ProgressDialog pDialog;
        int position;

        public process_get_taluka(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            queryBuilder.appendQueryParameter("id", "39");
            queryBuilder.appendQueryParameter("value", telman.getDeviceId());
            queryBuilder.appendQueryParameter("app_name", "hsn_sand_stock");
            queryBuilder.appendQueryParameter("combo_dist_code", ListDistrict.get(position).get("city_id") );
            queryBuilder.appendQueryParameter("ver_3",String.valueOf(Utils.VERSION) );
            params = queryBuilder.build().getEncodedQuery();
        }

        @Override
        protected JSONObject doInBackground(Void... args0)
        {
            try {
                return http.POST(mUrl,params);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject !=null)
            {
                try
                {
                    if (jsonObject.getJSONArray("arr_1").length() > 0)
                    {
                        mTaluka = new String[jsonObject.getJSONArray("arr_1").length()+1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("taluka_name","- Select Taluka -");
                        mItem.put("taluka_id","0");
                        mItem.put("city_id","0");
                        mTaluka[0] = "- Select Taluka -";
                        ListTaluka.add(mItem);
                        for (int i=0;i<jsonObject.getJSONArray("arr_1").length();i++)
                        {
                            mItem = new HashMap<>();
                            mItem.put("taluka_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("taluka_name"));
                            mItem.put("taluka_id",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("taluka_id"));
                            mItem.put("city_id",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("city_id"));
                            mTaluka[i+1] = jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("taluka_name");
                            ListTaluka.add(mItem);
                        }
                        ArrayAdapter<String> adpTaluka = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mTaluka );
                        spn_taluka.setAdapter(adpTaluka);
                    }
                    else
                    {
                        mTaluka = new String[1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("taluka_name","- Select Taluka -");
                        mItem.put("taluka_id","0");
                        mItem.put("city_id","0");
                        mTaluka[0] = "- Select Taluka -";
                        ListTaluka.add(mItem);
                        ArrayAdapter<String> adpTaluka = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mTaluka );
                        spn_taluka.setAdapter(adpTaluka);
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error",e.toString());
                    mTaluka = new String[1];
                    HashMap<String , String> mItem = new HashMap<>();
                    mItem.put("taluka_name","- Select Taluka -");
                    mItem.put("taluka_id","0");
                    mItem.put("city_id","0");
                    mTaluka[0] = "- Select Taluka -";
                    ListTaluka.add(mItem);
                    ArrayAdapter<String> adpTaluka = new ArrayAdapter<String>(Activity_NewTask.this,
                            android.R.layout.simple_spinner_item,mTaluka );
                    spn_taluka.setAdapter(adpTaluka);
                }
            }
            else
                Toast.makeText(Activity_NewTask.this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }
    public class process_get_village extends AsyncTask<Void , Void , JSONObject>
    {
        Uri.Builder queryBuilder = new Uri.Builder();
        Https http = new Https(Activity_NewTask.this);
        String params;
        ProgressDialog pDialog;
        int position;

        public process_get_village(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            queryBuilder.appendQueryParameter("id", "40");
            queryBuilder.appendQueryParameter("value", telman.getDeviceId());
            queryBuilder.appendQueryParameter("app_name", "hsn_sand_stock");
            queryBuilder.appendQueryParameter("taluk_code", ListTaluka.get(position).get("taluka_id") );
            queryBuilder.appendQueryParameter("ver_3",String.valueOf(Utils.VERSION) );
            params = queryBuilder.build().getEncodedQuery();
        }

        @Override
        protected JSONObject doInBackground(Void... args0)
        {
            try {
                return http.POST(mUrl,params);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject !=null)
            {
                try
                {
                    if (jsonObject.getJSONArray("arr_1").length() > 0)
                    {
                        mVillage= new String[jsonObject.getJSONArray("arr_1").length()+1];
                        villageId = new String[jsonObject.getJSONArray("arr_1").length()+1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("vill_id","0");
                        mItem.put("vill_name","- Select Village -");
                        mItem.put("taluka_id","0");
                        //mItem.put("taluka_name","");
                        //mItem.put("city_name","");
                        mVillage[0] = "- Select Village -";
                        villageId[0] = "0";
                        ListVillage.add(mItem);
                        for (int i=0;i<jsonObject.getJSONArray("arr_1").length();i++)
                        {
                            mItem = new HashMap<>();
                            mItem.put("vill_id",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("vill_id"));
                            mItem.put("vill_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("vill_name"));
                            mItem.put("taluka_id",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("taluka_id"));
                            //mItem.put("taluka_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("taluka_name"));
                            //mItem.put("city_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("city_name"));
                            mVillage[i+1] = jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("vill_name");
                            villageId[i+1] = jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("vill_id");
                            ListVillage.add(mItem);
                        }
                        ArrayAdapter<String> adpVillage = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mVillage );
                        spn_village.setAdapter(adpVillage);
                    }
                    else {
                        mVillage= new String[1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("vill_id","0");
                        mItem.put("vill_name","- Select Village -");
                        mItem.put("taluka_id","0");
                        mVillage[0] = "- Select Village -";
                        ListVillage.add(mItem);
                        ArrayAdapter<String> adpVillage = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mVillage );
                        spn_village.setAdapter(adpVillage);
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error",e.toString());
                    mVillage= new String[1];
                    HashMap<String , String> mItem = new HashMap<>();
                    mItem.put("vill_id","0");
                    mItem.put("vill_name","- Select Village -");
                    mItem.put("taluka_id","0");
                    mVillage[0] = "- Select Village -";
                    ListVillage.add(mItem);
                    ArrayAdapter<String> adpVillage = new ArrayAdapter<String>(Activity_NewTask.this,
                            android.R.layout.simple_spinner_item,mVillage );
                    spn_village.setAdapter(adpVillage);
                }
            }
            else
                Toast.makeText(Activity_NewTask.this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }

    public class process_get_main_dept extends AsyncTask<Void , Void , JSONObject>
    {
        Uri.Builder queryBuilder = new Uri.Builder();
        Https http = new Https(Activity_NewTask.this);
        String params;
        ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            queryBuilder.appendQueryParameter("id", "41");
            queryBuilder.appendQueryParameter("value", telman.getDeviceId());
            queryBuilder.appendQueryParameter("app_name", "hsn_sand_stock");
            queryBuilder.appendQueryParameter("ver_3",String.valueOf(Utils.VERSION) );
            params = queryBuilder.build().getEncodedQuery();
        }

        @Override
        protected JSONObject doInBackground(Void... args0)
        {
            try {
                return http.POST(mUrl,params);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject !=null)
            {
                try
                {
                    if (jsonObject.getJSONArray("arr_1").length() > 0)
                    {
                        mMainDept= new String[jsonObject.getJSONArray("arr_1").length()+1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("cust_type_name","- Select Main Dept -");
                        mItem.put("cust_type_code","0");
                        mItem.put("cust_type_desc","0");
                        mMainDept[0] = "- Select Main Dept -";
                        ListManDept.add(mItem);
                        for (int i=0;i<jsonObject.getJSONArray("arr_1").length();i++)
                        {
                            mItem = new HashMap<>();
                            mItem.put("cust_type_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("cust_type_name"));
                            mItem.put("cust_type_code",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("cust_type_code"));
                            mItem.put("cust_type_desc",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("cust_type_desc"));
                            mMainDept[i+1] = jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("cust_type_name");
                            ListManDept.add(mItem);
                        }
                        ArrayAdapter<String> adpMainDept = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mMainDept );
                        spn_main_dept.setAdapter(adpMainDept);
                    }
                    else
                    {
                        mMainDept= new String[1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("cust_type_name","- Select Main Dept -");
                        mItem.put("cust_type_code","0");
                        mItem.put("cust_type_desc","0");
                        mMainDept[0] = "- Select Main Dept -";
                        ListManDept.add(mItem);
                        ArrayAdapter<String> adpMainDept = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mMainDept );
                        spn_main_dept.setAdapter(adpMainDept);
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error",e.toString());
                    mMainDept= new String[1];
                    HashMap<String , String> mItem = new HashMap<>();
                    mItem.put("cust_type_name","- Select Main Dept -");
                    mItem.put("cust_type_code","0");
                    mItem.put("cust_type_desc","0");
                    mMainDept[0] = "- Select Main Dept -";
                    ListManDept.add(mItem);
                    ArrayAdapter<String> adpMainDept = new ArrayAdapter<String>(Activity_NewTask.this,
                            android.R.layout.simple_spinner_item,mMainDept );
                    spn_main_dept.setAdapter(adpMainDept);
                }
            }
            else
                Toast.makeText(Activity_NewTask.this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }
    public class process_get_sub_dept extends AsyncTask<Void , Void , JSONObject>
    {
        Uri.Builder queryBuilder = new Uri.Builder();
        Https http = new Https(Activity_NewTask.this);
        String params;
        ProgressDialog pDialog;
        int position;

        public process_get_sub_dept(int position) {
            this.position = position;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            queryBuilder.appendQueryParameter("id", "42");
            queryBuilder.appendQueryParameter("value", telman.getDeviceId());
            queryBuilder.appendQueryParameter("app_name", "hsn_sand_stock");
            queryBuilder.appendQueryParameter("ver_3",String.valueOf(Utils.VERSION) );
            queryBuilder.appendQueryParameter("dept_code", ListManDept.get(position).get("cust_type_code"));
            params = queryBuilder.build().getEncodedQuery();
        }

        @Override
        protected JSONObject doInBackground(Void... args0)
        {
            try {
                return http.POST(mUrl,params);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject !=null)
            {
                try
                {
                    if (jsonObject.getJSONArray("arr_1").length() > 0)
                    {
                        mSubDept= new String[jsonObject.getJSONArray("arr_1").length()+1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("dept_name","- Select Sub Dept -");
                        mItem.put("dept_id","0");
                        //mItem.put("cust_type_code","0");
                        //mItem.put("cust_type_name","0");
                        mSubDept[0] = "- Select Sub Dept -";
                        ListSubDept.add(mItem);
                        for (int i=0;i<jsonObject.getJSONArray("arr_1").length();i++)
                        {
                            mItem = new HashMap<>();
                            mItem.put("dept_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("dept_name"));
                            mItem.put("dept_id",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("dept_id"));
                            //mItem.put("cust_type_code",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("cust_type_code"));
                            //mItem.put("cust_type_name",jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("cust_type_name"));
                            mSubDept[i+1] = jsonObject.getJSONArray("arr_1").getJSONObject(i).getString("dept_name");
                            ListSubDept.add(mItem);
                            ArrayAdapter<String> adpSubDept = new ArrayAdapter<String>(Activity_NewTask.this,
                                    android.R.layout.simple_spinner_item,mSubDept );
                            spn_sub_dept.setAdapter(adpSubDept);
                        }
                    }
                    else
                    {
                        mSubDept= new String[1];
                        HashMap<String , String> mItem = new HashMap<>();
                        mItem.put("dept_name","- Select Sub Dept -");
                        mItem.put("dept_id","0");
                        mSubDept[0] = "- Select Sub Dept -";
                        ListSubDept.add(mItem);
                        ArrayAdapter<String> adpSubDept = new ArrayAdapter<String>(Activity_NewTask.this,
                                android.R.layout.simple_spinner_item,mSubDept );
                        spn_sub_dept.setAdapter(adpSubDept);
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error",e.toString());
                    mSubDept= new String[1];
                    HashMap<String , String> mItem = new HashMap<>();
                    mItem.put("dept_name","- Select Sub Dept -");
                    mItem.put("dept_id","0");
                    mSubDept[0] = "- Select Sub Dept -";
                    ListSubDept.add(mItem);
                    ArrayAdapter<String> adpSubDept = new ArrayAdapter<String>(Activity_NewTask.this,
                            android.R.layout.simple_spinner_item,mSubDept );
                    spn_sub_dept.setAdapter(adpSubDept);
                }
            }
            else
                Toast.makeText(Activity_NewTask.this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }
}
