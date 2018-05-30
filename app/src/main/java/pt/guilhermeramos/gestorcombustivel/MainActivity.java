package pt.guilhermeramos.gestorcombustivel;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pt.guilhermeramos.gestorcombustivel.models.DB;

public class MainActivity extends AppCompatActivity {

    public static final double MEDIA_CARRO = 5.5;
    private DB db;
    private Cursor c;
    private ListView lista;
    private EditText kmFinal;
    private EditText kmInicial;
    private EditText descricao;
    private Button inserir;
    private TextView totalKms;
    private TextView mesKm;
    private TextView aPagarKms;
    private TextView aPagarL;
    private Button pagarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lista = (ListView) findViewById(R.id.listaKms);
        descricao = (EditText) findViewById(R.id.descricao);
        kmInicial = (EditText) findViewById(R.id.kmInicial);
        kmFinal = (EditText) findViewById(R.id.kmFinal);
        inserir = (Button) findViewById(R.id.novaViagem);
        inserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = descricao.getText().toString();
                int kmI = Integer.parseInt(kmInicial.getText().toString());
                int kmF = Integer.parseInt(kmFinal.getText().toString());

                Date data = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                String dateInText = format.format(data);

                db.write("INSERT INTO viagens (descricao, kmInicial, kmFinal, pago, data) VALUES ('" + desc + "', " + kmI + ", " + kmF + ", " + (kmF - kmI) + " , '" + dateInText +"')");
                try {
                    refreshList();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        pagarButton = (Button) findViewById(R.id.pagarButton);
        pagarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PagarActivity.class);
                startActivity(intent);
            }
        });

        totalKms = (TextView) findViewById(R.id.totalKms);
        mesKm = (TextView) findViewById(R.id.mesKm);
        aPagarKms = (TextView) findViewById(R.id.aPagarKms);
        aPagarL = (TextView) findViewById(R.id.aPagarL);
    }


    @Override
    protected void onStart() {
        super.onStart();

        db = new DB(this);
        try {
            refreshList();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void refreshList() throws ParseException {
        c = db.read("SELECT * FROM viagens ORDER BY data DESC");
        MainActivity context = this;

        lista.setAdapter(new CursorAdapter(context, c, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(R.layout.viagem_item, parent, false);
                bindView(v, context, cursor);
                return v;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                String descricao = cursor.getString(cursor.getColumnIndex("descricao"));
                ((TextView) view.findViewById(R.id.descricaoM)).setText(descricao);

                int kmInicial = cursor.getInt(cursor.getColumnIndex("kmInicial"));
                ((TextView) view.findViewById(R.id.kmInitM)).setText("" + kmInicial + " Km");

                int kmFinal = cursor.getInt(cursor.getColumnIndex("kmFinal"));
                ((TextView) view.findViewById(R.id.kmFinalM)).setText("" + kmFinal + " Km");

                long pago = cursor.getLong(cursor.getColumnIndex("pago"));
                if (pago == 0) {
                    ((TextView) view.findViewById(R.id.descricaoM)).setTextColor(Color.GREEN);
                } else {
                    ((TextView) view.findViewById(R.id.descricaoM)).setTextColor(Color.RED);
                }
                ((TextView) view.findViewById(R.id.aPagarM)).setText("" + pago + " Km");

                String data = cursor.getString(cursor.getColumnIndex("data"));
                ((TextView) view.findViewById(R.id.dataM)).setText(data);
            }

        });

        int total = 0;
        int aPagar = 0;
        int mes = 0;
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                int kmI = c.getInt(c.getColumnIndex("kmInicial"));
                int kmF = c.getInt(c.getColumnIndex("kmFinal"));
                int p = c.getInt(c.getColumnIndex("pago"));
                int diff = kmF - kmI;

                total += diff;
                aPagar += p;

                String dataInString = c.getString(c.getColumnIndex("data"));
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                Date data = format.parse(dataInString);

                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();

                cal1.setTime(data);
                cal2.setTime(new Date());

                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                    if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                        mes += diff;
                    }
                }


                c.moveToNext();
            }
        }

        totalKms.setText(total + " Km");
        aPagarKms.setText(aPagar + " Km");
        mesKm.setText(mes + " Km");
        aPagarL.setText((aPagar * MEDIA_CARRO / 100) + " L");
    }
}
