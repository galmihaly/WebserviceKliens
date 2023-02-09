package hu.unideb.inf.webservicekliens;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import org.xml.sax.SAXException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.naming.Referenceable;

public class MainActivity extends AppCompatActivity {


    private Executor executor;
    private Button button;
    private static final String uuid1 = "5ac9cc40-fbf5-4c8b-90b4-d38d71f43c00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);

        executor = Executors.newSingleThreadExecutor();

        CallWebService callWebService = new CallWebService();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
        }

        button.setOnClickListener(view ->{
            executor.execute(()->{
//                callWebService.deviceLogin(callWebService.getDeviceName(), uuid1);
                callWebService.getConnection();
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            } else {
                //not granted
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private class CallWebService{


        public void deviceLogin(String deviceId, String programId){

            String xml = createPostBody(deviceId, programId);

            String stringURL = "http://global.mobileflex.hu/DeviceLogon.asmx?op=LogonDevice";

            URL url = null;
            try {
                url = new URL(stringURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type" ,"application/soap+xml; charset=utf-8");
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(xml);
                wr.flush();

                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                XMLParserHandler xmlParserHandler = new XMLParserHandler();

                saxParser.parse(connection.getInputStream(), xmlParserHandler);

                LogonDeviceResult logonDeviceResult = xmlParserHandler.getLogonDeviceResult();
                DataServiceDescriptor dataServiceDescriptor = xmlParserHandler.getDataServiceDescriptor();

                Log.e("", logonDeviceResult.deviceId + "  " + logonDeviceResult.applicationTypeId + "  " + logonDeviceResult.applicationRuntimeId + "  " + logonDeviceResult.possibleLogonTypes + "  " + logonDeviceResult.deviceLogonResult);
                Log.e("", dataServiceDescriptor.dataServiceType + "  " + dataServiceDescriptor.dataServiceConnectionDescriptor + "  " + dataServiceDescriptor.order);

                wr.close();

                getConnection();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        private void getConnection() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Connection connection;

            String _serverName = "DEATHSTAR";
            String _portNumber = "4241";
            String _databaseName = "MobileFlex";
            String _userId = "sa";
            String _password = "Gtr7jv8fh2";

            try{
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                String connectionUrl = "jdbc:jtds:sqlserver://server.logcontrol.hu:4241;user=Galmihaly;password=Gm2022!!!;databaseName=GalMihalyTest;";
                Connection con = DriverManager.getConnection(connectionUrl);

//                SQLServerDataSource ds = new SQLServerDataSource();

//                ds.setServerName("localhost\\sqlexpress");
////                ds.setPortNumber(Integer.parseInt("1433"));
//                ds.setDatabaseName("test");
//                ds.setEncrypt(false);

//                Connection con = ds.getConnection();
                Log.e("q", "connection után!!");
            }
            catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        public String createPostBody(String deviceId, String applicationId){

            String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                    "  <soap12:Body>\n" +
                    "    <LogonDevice xmlns=\"http://tempuri.org/\">\n" +
                    "      <deviceId>" + deviceId + "</deviceId>\n" +
                    "      <applicationTypeId>" + applicationId + "</applicationTypeId>\n" +
                    "    </LogonDevice>\n" +
                    "  </soap12:Body>\n" +
                    "</soap12:Envelope>";

            return body;
        }

        public String getDeviceName() {
            String serialNumber = null;
            Class<?> c = null;

            try {
                c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);

                serialNumber = (String) get.invoke(c, "gsm.sn1");

                if (serialNumber.equals("")) {
                    serialNumber = (String) get.invoke(c, "ril.serialnumber");
                }
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }

            if(serialNumber == null) return null;
            return serialNumber;
        }
    }
}