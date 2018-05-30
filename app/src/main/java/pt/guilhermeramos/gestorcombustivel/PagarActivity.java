package pt.guilhermeramos.gestorcombustivel;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import pt.guilhermeramos.gestorcombustivel.models.DB;

import static java.lang.Math.ceil;

public class PagarActivity extends AppCompatActivity {

    private Button pagar;
    private EditText editLitros;
    private TextView textKms;
    private DB db;
    private Cursor c;

    @Override
    protected void onStart() {
        super.onStart();
        db = new DB(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagar);
        pagar = (Button) findViewById(R.id.pagar);
        editLitros = (EditText) findViewById(R.id.totalLitros);
        textKms = (TextView) findViewById(R.id.kmAPagar);


        pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double litros;
                if(editLitros.getText().toString() == null || editLitros.getText().toString().isEmpty()) {

                    litros = 0.0;

                } else {

                    litros = Double.parseDouble(editLitros.getText().toString());

                }
                double kms = (litros * 100) / MainActivity.MEDIA_CARRO;

                // Update DB

                c = db.read("SELECT * FROM viagens ORDER BY data ASC");

                if (c.moveToFirst()) {
                    while (!c.isAfterLast()) {
                        int id = c.getInt(c.getColumnIndex("_id"));
                        long pago = c.getLong(c.getColumnIndex("pago"));

                        if (pago > 0) {
                            if (kms >= pago) {
                                kms -= pago;
                                pago = 0;
                            } else {
                                pago -= kms;
                                kms = 0;
                            }

                            db.write("UPDATE viagens SET pago = " + pago + " WHERE _id = " + id);
                        }

                        if (kms == 0)
                            break;

                        c.moveToNext();
                    }
                }

                Intent intent = new Intent(PagarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        editLitros.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    double litros = Double.parseDouble(s.toString());
                    double kms = (litros * 100) / MainActivity.MEDIA_CARRO;
                    textKms.setText(ceil(kms) + " Km");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
