package com.example.asus.student;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import StudentDBHelper.StudentDBHelper;
import Student.Student;
import TableContanst.TableContanst;
public class AddStudentActivity extends Activity implements OnClickListener {
    private static final String TAG = "AddStudentActivity";
    private final static int DATE_DIALOG = 1;
    private static final int DATE_PICKER_ID = 1;
    private TextView idText;
    private EditText nameText;
    private EditText ageText;
    private EditText phoneText;
    private EditText dataText;
    private RadioGroup group;
    private RadioButton button1;
    private RadioButton button2;
    private CheckBox box1;
    private CheckBox box2;
    private CheckBox box3;
    private Button restoreButton;
    private String sex;
    private Button resetButton;
    private Long student_id;
    private StudentDao dao;
    private boolean isAdd = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student);
        idText = (TextView) findViewById(R.id.tv_stu_id);
        nameText = (EditText) findViewById(R.id.et_name);
        ageText = (EditText) findViewById(R.id.et_age);
        button1 = (RadioButton) findViewById(R.id.rb_sex_female);
        button2 = (RadioButton) findViewById(R.id.rb_sex_male);
        phoneText = (EditText) findViewById(R.id.et_phone);
        dataText = (EditText) findViewById(R.id.et_traindate);
        group = (RadioGroup) findViewById(R.id.rg_sex);
        box1 = (CheckBox) findViewById(R.id.box1);
        box2 = (CheckBox) findViewById(R.id.box2);
        box3 = (CheckBox) findViewById(R.id.box3);
        restoreButton = (Button) findViewById(R.id.btn_save);
        resetButton = (Button) findViewById(R.id.btn_clear);
        dao = new StudentDao(new StudentDBHelper(this)); // ???????????? 78
        restoreButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        dataText.setOnClickListener(this);
        checkIsAddStudent();
    }
    // ????????????Activity??????????????????????????????
    private void checkIsAddStudent() {
        Intent intent = getIntent();
        Serializable serial = intent.getSerializableExtra(TableContanst.STUDENT_TABLE);
        if (serial == null) {
            isAdd = true;
            dataText.setText(getCurrentDate());
        } else {
            isAdd = false;
            Student s = (Student) serial;
            showEditUI(s);
        }
    }
    //???????????????????????????UI104
    private void showEditUI(Student student) {
        // ??????Student????????????????????????student????????????????????????
        student_id = student.getId();
        String name = student.getName();
        int age = student.getAge();
        String phone = student.getPhoneNumber();
        String data = student.getTrainDate();
        String like = student.getLike();
        String sex = student.getSex();
        if (sex.toString().equals("???")) {
            button2.setChecked(true);
        } else if (sex.toString().equals("???")) {
            button1.setChecked(true);
        }
        if (like != null && !"".equals(like)) {
            if (box1.getText().toString().indexOf(like) >= 0) {
                box1.setChecked(true);
            }
            if (box2.getText().toString().indexOf(like) >= 0) {
                box2.setChecked(true);
            }
            if (box3.getText().toString().indexOf(like) >= 0) {
                box3.setChecked(true);
            }
        }
        // ????????????
        idText.setText(student_id + "");
        nameText.setText(name + "");
        ageText.setText(age + "");
        phoneText.setText(phone + "");
        dataText.setText(data + "");
        setTitle("??????????????????");
        restoreButton.setText("??????");
    }
    public void onClick(View v) {
        // ????????????
        if (v == restoreButton) {
            if (!checkUIInput()) {// ??????????????????
                return;
            }
            Student student = getStudentFromUI();
            if (isAdd) {
                long id = dao.addStudent(student);
                dao.closeDB();
                if (id > 0) {
                    Toast.makeText(this, "??????????????? ID=" + id,Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
            } else if (!isAdd) {
                long id = dao.addStudent(student);
                dao.closeDB();
                if (id > 0) {
                    Toast.makeText(this, "????????????",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "?????????????????????????????????",Toast.LENGTH_SHORT).show();
                }
            }
        } else if (v == resetButton) {
            clearUIData();
        } else if (v == dataText) {
            showDialog(DATE_PICKER_ID);
        }
    }
    //       ?????????????????????176
    private void clearUIData() {
        nameText.setText("");
        ageText.setText("");
        phoneText.setText("");
        dataText.setText("");
        box1.setChecked(false);
        box2.setChecked(false);
        group.clearCheck();
    }
    //      ?????????????????????????????????????????????Student??????
    private Student getStudentFromUI() {
        String name = nameText.getText().toString();
        int age = Integer.parseInt(ageText.getText().toString());
        String sex = ((RadioButton) findViewById(group
                .getCheckedRadioButtonId())).getText().toString();
        String likes = "";
        if (box1.isChecked()) { // basketball, football football
            likes += box1.getText();
        }
        if (box2.isChecked()) {
            if (likes.equals("")) {
                likes += box2.getText();
            } else {
                likes += "," + box2.getText();
            }
            if (likes.equals("")) {
                likes += box3.getText();
            } else {
                likes += "," + box3.getText();
            }
        }
        String trainDate = dataText.getText().toString();
        String phoneNumber = phoneText.getText().toString();
        String modifyDateTime = getCurrentDateTime();
        Student s=new Student(name, age, sex, likes, phoneNumber, trainDate,
                modifyDateTime);
        if (!isAdd) {
            s.setId(Integer.parseInt(idText.getText().toString()));
            dao.deleteStudentById(student_id);
        }
        return s;
    }
    //      * ???????????????????????????
    private String getCurrentDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }
    //      * ?????????????????????
    private String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }
    //??????????????????????????????????????????
    private boolean checkUIInput() { // name, age, sex
        String name = nameText.getText().toString();
        String age = ageText.getText().toString();
        int id = group.getCheckedRadioButtonId();
        String message = null;
        View invadView = null;
        if (name.trim().length() == 0) {
            message = "??????????????????";
            invadView = nameText;
        } else if (age.trim().length() == 0) {
            message = "??????????????????";
            invadView = ageText;
        } else if (id == -1) {
            message = "??????????????????";
        }
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (invadView != null)
                invadView.requestFocus();
            return false;
        }         return true;     }
    //????????????????????????
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dataText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        }
    };
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, onDateSetListener, 2011, 8, 14);
        }
        return null;
    }
}