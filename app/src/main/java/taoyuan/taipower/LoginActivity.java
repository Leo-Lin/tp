package taoyuan.taipower;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changePageToLogin();
    }

    public void doLogin(View view) {
        EditText editTextAccount = (EditText) findViewById(R.id.editTextAccount);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        String account = editTextAccount.getText().toString();
        String password = editTextPassword.getText().toString();

        final ProgressDialog progressDialog = ProgressDialog.show(this, "登入中", "請稍候");
        ParseUser.logInInBackground(account, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                progressDialog.dismiss();

                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("userId", user.getObjectId());
                installation.saveInBackground();

                if (user == null) {
                    Toast.makeText(LoginActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    public void goRegister(View view) {
        changePageToRegister();
    }

    public void backToLogin(View view) {
        changePageToLogin();
    }

    private void changePageToLogin() {
        setContentView(R.layout.activity_login);
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        getSupportActionBar().setTitle("登入");
    }

    private void changePageToRegister() {
        setContentView(R.layout.layout_register);
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        getSupportActionBar().setTitle("註冊");
    }

    public void doRegister(View view) {
        EditText editTextAccount = (EditText) findViewById(R.id.editTextAccount);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        final String account = editTextAccount.getText().toString();
        String password = editTextPassword.getText().toString();

        final ProgressDialog progressDialog = ProgressDialog.show(this, "註冊中", "請稍候");
        ParseUser user = new ParseUser();
        user.setUsername(account);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(LoginActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();
                    changePageToLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "註冊失敗", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
