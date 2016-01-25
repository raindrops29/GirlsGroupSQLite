package com.pyo.in.soo.sqlite;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText memberName, memberMemo, groupName;
    ImageView memberImage, memberImageGallery;
    Button saveBtn;
    TextView memberBirth;
    Spinner companyName;
    static final int PICK_FROM_GALLERY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memberName = (EditText) findViewById(R.id.regit_edit_name);
        memberMemo = (EditText) findViewById(R.id.regit_edit_memo);
        groupName = (EditText) findViewById(R.id.group_name);

        saveBtn = (Button) findViewById(R.id.member_save);

        memberImage = (ImageView) findViewById(R.id.regit_iv_image);
        memberImageGallery = (ImageView) findViewById(R.id.regit_iv_image_gallery);
        companyName = (Spinner) findViewById(R.id.company_name);

        memberImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // 잘라내기 셋팅
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 0);
                intent.putExtra("aspectY", 0);
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 150);
                try {
                    intent.putExtra("return-data", true);
                    startActivityForResult(Intent.createChooser(intent,
                            "소녀시대 멤버선택"), PICK_FROM_GALLERY);
                } catch (ActivityNotFoundException e) {
                    Log.e("imageGallery", e.toString());
                }
            }
        });
        memberBirth = (TextView) findViewById(R.id.regit_tv_birth);
        Calendar currentCalendar = Calendar.getInstance();
        final int tempYear = currentCalendar.get(Calendar.YEAR) - 20;
        final int tempMonth = currentCalendar.get(Calendar.MONTH) + 1;
        final int tempDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        memberBirth.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(MainActivity.this,
                                mDateSetListener, tempYear, tempMonth, tempDay).show();

                    }
                    DatePickerDialog.OnDateSetListener mDateSetListener =
                            new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker view,
                                                      int year, int monthOfYear,
                                                      int dayOfMonth) {
                                    updateNow(year, monthOfYear, dayOfMonth);
                                }
                            };
                }

        );
    }  //이미지 셋팅

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
                //  if (Build.VERSION.SDK_INT < 19) {
                Uri selectedImage = data.getData();
                //Log.e("selectedImage", String.valueOf(selectedImage.toString()));
               /* String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                fileLocation = cursor.getString(columnIndex);
                Log.e("fileLocation", String.valueOf(fileLocation));
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                */
                memberImage.setImageURI(selectedImage);
                fileLocation =selectedImage.toString();
                //memberImage.setImageBitmap(BitmapFactory.decodeFile(fileLocation, options));
                /* } else {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    memberImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                            memberImage.getMeasuredWidth(),
                            memberImage.getMeasuredHeight(), false));
                    memberImage.setImageBitmap(bitmap);

                }*/
            }
        } catch (OutOfMemoryError error) {
            Log.e("OutofMemoryError", error.toString());
        }
    }

    private void updateNow(int mYear, int mMonth, int mDay) {
        memberBirth.setText
                (String.format("%d/%d/%d", mYear, mMonth + 1, mDay));

    }

    String fileLocation; //null발생시 추가코드 필요함
    String fileImageDir;

    ArrayAdapter<String> spinnerAdapter;
    String memberCompany;
    ArrayList<String> memberCompanys;

    @Override
    public void onResume() {
        super.onResume();

        memberCompanys = GirlsGroupSQLiteOpenHelper.getInstance().getCompanyNames();


        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, memberCompanys);
        companyName.setAdapter(spinnerAdapter);
        companyName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                memberCompany = memberCompanys.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                memberCompany = "SM";
            }
        });
        //SD카드가 없을시 에러처리
        String sdcardMounted = Environment.getExternalStorageState();
        if (sdcardMounted.equals(Environment.MEDIA_MOUNTED) == false) {
            Toast.makeText(this, "SD 카드가 없어 종료 합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        fileImageDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/com/pyo/in/soo/sqlite/";

        File imageStoreDir = new File(fileImageDir);
        /*if (!imageStoreDir.exists()) {
            if (imageStoreDir.mkdirs()) {

            }
        }*/
    }

    /*
      android:onClick="insertMemberInfo"
     */
    public void insertMemberInfo(View v) {
        if (validationCheck()) {
            GirlsGroupValueObject valueObject = new GirlsGroupValueObject();
            valueObject.companyName = memberCompany;
            valueObject.memberMemo = memberMemo.getText().toString().trim();
            valueObject.groupName = groupName.getText().toString().trim();
            valueObject.memberBirth = memberBirth.getText().toString().trim();
            valueObject.memberName = memberName.getText().toString().trim();
            Log.e("파일URi", String.valueOf(fileLocation));
            if (fileLocation != null) {
                valueObject.memberImageURI = fileLocation;
            } else {
                valueObject.memberImageURI = "no_image";
            }
            boolean resultedFlag = GirlsGroupSQLiteOpenHelper.getInstance().insertGirlsGroup(valueObject);
            if (resultedFlag) {
                Toast.makeText(MainActivity.this, " 입력됨", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, " 문제발생~~디버깅", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validationCheck() {
        if (memberName.getText().toString().trim().equals("") || memberMemo.getText().toString().trim().equals("") ||
                memberBirth.getText().toString().trim().equals("") || groupName.getText().toString().trim().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("경고");
            builder.setMessage("모든 정보를 입력해야 등록이 가능합니다.");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setCancelable(false);
            builder.setNegativeButton("확인", null);
            builder.show();
            return false;
        }
        return true;
    }
}
