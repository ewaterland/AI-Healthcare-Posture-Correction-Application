package com.example.hcd;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // 입력 있으면 1, 입력 없으면 0
    int email_active = 0;
    int pw_active = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();

        EditText editText_email = (EditText) findViewById(R.id.editText_EmailAddress);
        EditText editText_password = (EditText) findViewById(R.id.editText_Password);

        editText_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { email_active = 1; check(); }
                else              { email_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { pw_active = 1; check(); }
                else              { pw_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // 이메일과 비밀번호가 둘 다 입력됐는지 확인
    public void check()
    {
        Button btn_login = (Button) findViewById(R.id.btn_signin);

        if (email_active == 1 && pw_active == 1)
        {
            btn_login.setEnabled(true);
        }
        else
        {
            btn_login.setEnabled(false);
        }
    }

    // Sign up 버튼 누를 경우 회원가입 화면으로 이동
    public void Register(View target)
    {
        Intent intent = new Intent(getApplication(), RegisterActivity.class);
        startActivity(intent);
    }

    // Sign in 버튼 누를 경우 로그인 시도
    public void Login(View target)
    {
        EditText editText_email = (EditText) findViewById(R.id.editText_EmailAddress);
        EditText editText_password = (EditText) findViewById(R.id.editText_Password);

        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();

        if (email.length() == 0 && password.length() == 0)
        {
            Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (email.length() == 0)
        {
            Toast.makeText(LoginActivity.this, "이메일을 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (password.length() == 0)
        {
            Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            // 로그인 요청
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) // 로그인 성공
                    {
                        Toast.makeText(LoginActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

                        // 홈 화면으로 이동
                        Intent intent = new Intent(getApplication(), PoseActivity.class);
                        startActivity(intent);
                        finish();

                        Log.d(TAG, "< 로그인 성공 >");
                    }
                    else // 로그인 실패
                    {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            Toast.makeText(LoginActivity.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();

                            Log.w(TAG, "< 로그인 실패 - 이메일 형식 오류 >", task.getException());
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();

                            Log.w(TAG, "< 로그인 실패 - 기타 오류 >", task.getException());
                        }
                    }
                }
            });
        }
    }
}