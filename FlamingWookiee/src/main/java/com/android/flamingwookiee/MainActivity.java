package com.android.flamingwookiee;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submit = (Button) findViewById(R.id.send_url_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQuiz();
            }
        });
    }

    public void launchQuiz() {
        // http://localhost:8080/quiz/1234
        EditText urlBox = (EditText) findViewById(R.id.url);
        urlBox.setError(null);

        try {
            URL url = new URL(urlBox.getText().toString());
            Intent intent = new Intent(this, QuizActivity.class);
            //TODO get rid of host and port issues
            if (url.getPort() == -1) {
                throw new MalformedURLException("bad");
            }
            intent.putExtra("host", url.getHost()+":"+url.getPort());
            if (url.getPath().length() < 6) {
                throw new MalformedURLException("bad");
            }
            intent.putExtra("qid", Integer.parseInt(url.getPath().substring(6)));
            startActivity(intent);
        } catch (MalformedURLException e) {
            //TODO better error checking...
            urlBox.setError("Bad URL, try again");
            urlBox.requestFocus();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getSharedPreferences("info", MODE_PRIVATE).contains("pin_field")) {
            showSettings();
        }
    }

    public void showSettings() {
        DialogFragment newFragment = new InfoEntryFragment();
        newFragment.show(getFragmentManager(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
