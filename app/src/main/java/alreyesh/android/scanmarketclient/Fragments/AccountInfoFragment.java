package alreyesh.android.scanmarketclient.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.security.Permission;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import alreyesh.android.scanmarketclient.R;

public class AccountInfoFragment extends Fragment {
    private static String ip="35.198.32.112";
    private static String port ="3306" ;
    private static String Classes="net.sourceforge.jtds.jdbc.Driver";
    private static String database="testdb";
    private static String username="sqlserver";
    private static String password="sqlserver";
    private static String url = "jdbc:jtds:sqlserver//"+ip+":"+port+"/"+database;
    private Connection connection = null;
    private TextView txtPrueba;
    private Button btnPrueba;
    private FirebaseAuth mAuth;

    public AccountInfoFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        View view =inflater.inflate(R.layout.fragment_account_info, container, false);
           StrictMode.ThreadPolicy policity = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policity);

            txtPrueba = view.findViewById(R.id.txtPrueba);
            btnPrueba = view.findViewById(R.id.btnSQL);
            try{
                Class.forName(Classes);
                connection = DriverManager.getConnection(url,username,password);
                txtPrueba.setText("SUCCESS");
            }catch(ClassNotFoundException e){
                e.printStackTrace();
                txtPrueba.setText("ERROR");
            }catch(SQLException e){
                e.printStackTrace();
                txtPrueba.setText("FAILURE");
            }

            btnPrueba.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(connection != null){
                        Statement statement = null;
                        try{
                            statement = connection.createStatement();
                            ResultSet resultSet = statement.executeQuery("select * from TEST_TABLE");
                            while(resultSet.next()){
                                txtPrueba.setText(resultSet.getString(1));
                            }
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                    }else{
                        txtPrueba.setText("Connection is null");
                    }
                }
            });

        return  view;
    }
}