package com.hb.cultivos.cultivos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SecondaryActivity extends AppCompatActivity {

    Spinner mEstado;
    Spinner mMuestra;
    int idPaciente;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this.getApplicationContext();

        final EditText mNombrePaciente = (EditText) findViewById(R.id.nombre_paciente);
        mEstado = (Spinner) findViewById(R.id.estado);
        mMuestra = (Spinner) findViewById(R.id.muestra);
        final EditText mGermen = (EditText) findViewById(R.id.germen);
        final EditText mSensibilidad = (EditText) findViewById(R.id.sensibilidad);
        final EditText mResistencia = (EditText) findViewById(R.id.resistencia);
        final Button mGuardar = (Button) findViewById(R.id.guardar);

        loadSpinnerData("http://192.168.0.104/estados.php",mEstado);
        loadSpinnerData("http://192.168.0.104/muestras.php",mMuestra);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.0.104/cultivo.php?id=1";

        // Request a string response from the provided URL.
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        try {
                            JSONObject json = new JSONObject(response.toString());
                            idPaciente = json.getInt("ID");
                            mNombrePaciente.setText(json.getString("NOMBRE_PACIENTE"));
                            mEstado.setSelection(json.getInt("ID_ESTADO")-1);
                            mMuestra.setSelection(json.getInt("ID_MUESTRA")-1);
                            mGermen.setText(json.getString("GERMEN"));
                            mResistencia.setText(json.getString("RESISTENCIA"));
                            mSensibilidad.setText(json.getString("SENSIBILIDAD"));
                            mGuardar.setOnClickListener(new View.OnClickListener () {
                                public void onClick(View v) {
                                    try {
                                        JSONObject json = new JSONObject();
                                        json.put("ID", idPaciente);
                                        json.put("NOMBRE_PACIENTE",mNombrePaciente.getText());
                                        json.put("ID_ESTADO",((SpinnerItem)mEstado.getSelectedItem()).id);
                                        json.put("ID_MUESTRA",((SpinnerItem)mMuestra.getSelectedItem()).id);
                                        json.put("GERMEN",mGermen.getText());
                                        json.put("RESISTENCIA",mResistencia.getText());
                                        json.put("SENSIBILIDAD",mSensibilidad.getText());
                                        Log.w("json salida",json.toString());
                                        RequestQueue queue = Volley.newRequestQueue(context);
                                        String url ="http://192.168.0.104/actualizar.php";
                                        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,json,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Log.w("error1",response.toString());
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.w("error2",error.getMessage());
                                            }
                                        });
                                        queue.add(jsonRequest);
                                    } catch (JSONException e){
                                    }
                                }
                            });
                        } catch (Exception e){
                            Log.w("error",e.getCause());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mNombrePaciente.setText("That didn't work!");
                Log.w("error","That didn't work!");
            }


        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadSpinnerData(String url, final Spinner spinner) {
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    ArrayList<SpinnerItem> spinnerMap = new ArrayList<SpinnerItem>();
                    JSONArray json= new JSONArray(response.toString());
                    for(int i=0;i<json.length();i++){
                        JSONObject jsonObject1=json.getJSONObject(i);
                        Integer id=jsonObject1.getInt("ID");
                        String estado=jsonObject1.getString("NOMBRE");
                        spinnerMap.add(new SpinnerItem(id,estado));
                    }

                    spinner.setAdapter(new ArrayAdapter<SpinnerItem>(SecondaryActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerMap));

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }

        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }



}
