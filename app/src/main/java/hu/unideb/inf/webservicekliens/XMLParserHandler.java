package hu.unideb.inf.webservicekliens;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.UUID;

public class XMLParserHandler extends DefaultHandler {

    private static final String DEVICE_ID = "DeviceId";
    private static final String APPLICATON_TYPE_ID = "ApplicationTypeId";
    private static final String APPLICATION_RUNTIME_ID = "ApplicationRuntimeId";
    private static final String POSSIBLE_LOGON_TYPES = "PossibleLogonTypes";
    private static final String DEVICE_LOGON_RESULT = "DeviceLogonResult";
    private static final String DATASERVICE_TYPE = "DataServiceType";
    private static final String DATASERVICE_CONNECTION_DESCRIPTOR = "DataServiceConnectionDescriptor";
    private static final String ORDER = "Order";

    private boolean isDeviceId = false;
    private boolean isApplicationTypeId = false;
    private boolean isApplicationRuntimeId = false;
    private boolean isPossibleLogonTypes = false;
    private boolean isDeviceLogonResult = false;
    private boolean isDataServiceType = false;
    private boolean isDataServiceConnectionDescriptor = false;
    private boolean isOrder = false;

    private LogonDeviceResult logonDeviceResult = new LogonDeviceResult();
    private DataServiceDescriptor dataServiceDescriptor = new DataServiceDescriptor();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        switch (qName){
            case DEVICE_ID: isDeviceId = true; break;
            case APPLICATON_TYPE_ID: isApplicationTypeId = true; break;
            case APPLICATION_RUNTIME_ID: isApplicationRuntimeId = true; break;
            case POSSIBLE_LOGON_TYPES: isPossibleLogonTypes = true; break;
            case DEVICE_LOGON_RESULT: isDeviceLogonResult = true; break;
            case DATASERVICE_TYPE: isDataServiceType = true; break;
            case DATASERVICE_CONNECTION_DESCRIPTOR: isDataServiceConnectionDescriptor = true; break;
            case ORDER: isOrder = true; break;
            default: break;
        }

    }

//    @Override
//    public void endElement(String uri, String localName, String qName) throws SAXException {
//        super.endElement(uri, localName, qName);
//    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        if(isDeviceId){
            Log.d("", new String(ch, start, length));
            logonDeviceResult.deviceId = new String(ch, start, length);
            isDeviceId = false;
        }
        else if(isApplicationTypeId){
            Log.d("", new String(ch, start, length));
            logonDeviceResult.applicationTypeId = UUID.fromString(new String(ch, start, length));
            isApplicationTypeId = false;
        }
        else if(isApplicationRuntimeId){
            Log.d("", new String(ch, start, length));
            logonDeviceResult.applicationRuntimeId = UUID.fromString(new String(ch, start, length));
            isApplicationRuntimeId = false;
        }
        else if(isPossibleLogonTypes){
            Log.d("", new String(ch, start, length));
            logonDeviceResult.possibleLogonTypes = Integer.parseInt(new String(ch, start, length));
            isPossibleLogonTypes = false;
        }
        else if(isDeviceLogonResult){
            Log.d("", new String(ch, start, length));
            logonDeviceResult.deviceLogonResult = new String(ch, start, length);
            isDeviceLogonResult = false;
        }
        else if(isDataServiceType){
            Log.d("", new String(ch, start, length));
            dataServiceDescriptor.dataServiceType = new String(ch, start, length);
            isDataServiceType = false;
        }
        else if(isDataServiceConnectionDescriptor){
            Log.d("", new String(ch, start, length));
            dataServiceDescriptor.dataServiceConnectionDescriptor = new String(ch, start, length);
            isDataServiceConnectionDescriptor = false;
        }
        else if(isOrder){
            Log.d("", new String(ch, start, length));
            dataServiceDescriptor.order = Integer.parseInt(new String(ch, start, length));
            isOrder = false;
        }
    }

    public LogonDeviceResult getLogonDeviceResult() {
        return logonDeviceResult;
    }

    public DataServiceDescriptor getDataServiceDescriptor() {
        return dataServiceDescriptor;
    }
}
