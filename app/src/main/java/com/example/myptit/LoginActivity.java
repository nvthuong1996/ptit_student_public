package com.example.myptit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;




/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Intent messageIntent = getIntent();
        int code = messageIntent.getIntExtra("requestCode",1);
        String mssv = messageIntent.getStringExtra("mssv");
        if(code == 2){
            mEmailView.setText(mssv);
            mEmailView.setEnabled(false);
            Button p1_button = (Button)findViewById(R.id.email_sign_in_button);
            p1_button.setText("Cập nhập");
        }

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Toast.makeText(getApplicationContext(), "Starting API", Toast.LENGTH_LONG).show();
        UserLoginTask startAPI = new UserLoginTask("", "",true);
        startAPI.execute((Void) null);

    }



    /**
     * Callback received when a permissions request has been completed.
     */

    @Override
    public void onBackPressed() {
        Intent messageIntent = getIntent();
        int code = messageIntent.getIntExtra("requestCode",1);
        if(code == 2){
            super.onBackPressed();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            showProgress(true);
            mAuthTask = new UserLoginTask(email, password,false);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private final String  API = "https://young-sea-10215.herokuapp.com/";
        //private final String  API = "http://192.168.0.101:3000/";
        private final boolean isStart;
        private final int timeout;
        private String error;

        UserLoginTask(String email, String password,boolean isStart) {
            mEmail = email;
            mPassword = password;
            this.isStart = isStart;
            this.timeout = 50000;
        }

        public void saveDataDefault(String content){
            String fileName = "data.json";
            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(content.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(this.isStart ){
                    String json=Jsoup.connect(this.API).timeout(this.timeout).ignoreContentType(true).execute().body();
                    return true;
                }
                String apicall = this.API + "api?username="+mEmail+"&password="+mPassword;
                String json=Jsoup.connect(apicall).timeout(this.timeout).ignoreContentType(true).execute().body();
                boolean isSuccess = new JSONObject(json).getBoolean("success");
                if(isSuccess){
                    saveDataDefault(json);
                    finish();
                }else{
                    String error = new JSONObject(json).getString("error");
                    this.error = error;
                    return false;
                }
            }catch (SocketTimeoutException e){
                if(!this.isStart){
                    this.error = "Kết nối hết hạn. Hãy thử lại";
                }
                return false;
            }
            catch (UnknownHostException e) {
                if(!this.isStart){
                    this.error = "Lỗi kết nối mạng";
                }else{
                    this.error = "Lỗi kết nối mạng";
                }
                return false;
            }catch (Exception e){
                if(this.isStart){
                    this.error = "Lỗi khi khởi động API. Hãy báo cho Admin";
                }else{
                    this.error = e.toString();
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(this.isStart){
                if(success){
                    Toast.makeText(getApplicationContext(), "Đã sẵn sàng để login", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), this.error, Toast.LENGTH_SHORT).show();
                }
            }else{
                mAuthTask = null;
                showProgress(false);
                if (success) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Hệ thống hay bị tắc nghẽn. Bạn cần phải thử login vài lần hoặc login sau vài phút", Toast.LENGTH_LONG).show();
                    mPasswordView.setError(this.error);
                    mPasswordView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            if(!this.isStart){
                mAuthTask = null;
                showProgress(false);
            }
        }
    }
}

