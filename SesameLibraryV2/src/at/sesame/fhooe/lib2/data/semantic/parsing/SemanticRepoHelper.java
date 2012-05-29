package at.sesame.fhooe.lib2.data.semantic.parsing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.util.Log;
import at.sesame.fhooe.lib2.data.SesameSensor.SensorType;

public class SemanticRepoHelper 
{
	private static final String TAG = "SemanticRepoHelper";
	public static final SimpleDateFormat OPEN_RDF_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//	private static final String OPEN_RDF_DATE_POSTFIX = "\"^^xsd:dateTime";
	
	
	public static final String DEFAULT_PREFIX_KEY = "default";
	public static final String RDFS_PREFIX_KEY = "rdfs";
	public static final String PSYS_PREFIX_KEY = "psys";
	public static final String OWL_PREFIX_KEY = "owl";
	public static final String XSD_PREFIX_KEY = "xsd";
	public static final String RDF_PREFIX_KEY = "rdf";
	public static final String PEXT_PREFIX_KEY = "pext";
	
	public static final String VALUES_QUERY_VALUE_KEY = "sensorValue";
	public static final String VALUES_QUERY_TIMESTAMP_KEY = "timestamp";
	
	public static HashMap<String, String> PREFIXES = new HashMap<String, String>();

	static
	{
		PREFIXES.put(DEFAULT_PREFIX_KEY, "http://www.sesame-s.ftw.at/ontologies/2012/1/SmartBuilding.owl#");
		PREFIXES.put(RDFS_PREFIX_KEY, "http://www.w3.org/2000/01/rdf-schema#");
		PREFIXES.put(PSYS_PREFIX_KEY, "http://proton.semanticweb.org/protonsys#");
		PREFIXES.put(OWL_PREFIX_KEY, "http://www.w3.org/2002/07/owl#");
		PREFIXES.put(XSD_PREFIX_KEY, "http://www.w3.org/2001/XMLSchema#");
		PREFIXES.put(RDF_PREFIX_KEY, "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		PREFIXES.put(PEXT_PREFIX_KEY, "http://proton.semanticweb.org/protonext#");
	}
	
	public static String getSensorsQuery(SensorType _st)
	{
		StringBuilder queryBuilder = new StringBuilder();
//		queryBuilder.append("select ?x  ?y where { ?x <");
		queryBuilder.append("select ?x ?y where { ?x <");
		queryBuilder.append(PREFIXES.get(RDF_PREFIX_KEY));
		queryBuilder.append("type> <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("Location> . ");
		queryBuilder.append("?x <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("locationHas> ?y . ?y <");
		queryBuilder.append(PREFIXES.get(RDF_PREFIX_KEY));
		queryBuilder.append("type> <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
//		queryBuilder.append("> ");
		switch(_st)
		{
		case light:
			queryBuilder.append("LightSensor");
			break;
		case energy:
			queryBuilder.append("Meter");
			break;
		case humidity:
			queryBuilder.append("HumiditySensor");
			break;
		case temperature:
			queryBuilder.append("TemperatureSensor");
			break;
		}
		
		queryBuilder.append(">}");
		Log.i(TAG, queryBuilder.toString());
		return queryBuilder.toString();
	}
	
//	public static String getMeasurementPlacesQuery(SensorType _st)
//	{
//		StringBuilder queryBuilder = new StringBuilder();
////		queryBuilder.append("select ?x  ?y where { ?x <");
//		queryBuilder.append("select ?x where { ?x <");
//		queryBuilder.append(PREFIXES.get(RDF_PREFIX_KEY));
//		queryBuilder.append("type> <");
//		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
//		queryBuilder.append("Location> . ");
//		queryBuilder.append("?x <");
//		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
//		queryBuilder.append("locationHas> ?y . ?y <");
//		queryBuilder.append(PREFIXES.get(RDF_PREFIX_KEY));
//		queryBuilder.append("type> <");
//		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
////		queryBuilder.append("> ");
//		switch(_st)
//		{
//		case light:
//			queryBuilder.append("LightSensor");
//			break;
//		case energy:
//			queryBuilder.append("Meter");
//			break;
//		case humidity:
//			queryBuilder.append("HumiditySensor");
//			break;
//		case temperature:
//			queryBuilder.append("TemperatureSensor");
//			break;
//		}
//		
//		queryBuilder.append(">}");
//		Log.i(TAG, queryBuilder.toString());
//		return queryBuilder.toString();
//	}
	
	public static String getSensorValuesQuery(String _sensorId, Date _start, Date _end)
	{
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select ?y ?z  where { <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append(_sensorId);
		queryBuilder.append("> <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("hasInstantMeasurement> ?x . ?x <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("atInstant> ?y . ?x <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("hasMeasurement> ?z . filter (?y <");
		queryBuilder.append(getRDFTimeString(_end));
		queryBuilder.append(") . filter( ?y > ");
		queryBuilder.append(getRDFTimeString(_start));
		queryBuilder.append(")} order by ?y");
		Log.i(TAG, queryBuilder.toString());
		return queryBuilder.toString();
	}
	
	public static String getEnhancedNotificationQuery(String _room, String _sensorId, Date _start, Date _end)
	{
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select ?room ?sensor ?alert ?time ?message where{?alert <");
		queryBuilder.append(PREFIXES.get(RDF_PREFIX_KEY));
		queryBuilder.append("type> <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("LightAlert> . ?alert <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("atInstant> ?time . ?alert <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("alertFromLightDevice> ?sensor . ?alert <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("hasAlertMesage> ?message . ?sensor <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("locatedAt> ?room. filter(?time >= ");
		queryBuilder.append(getRDFTimeString(_start));
		queryBuilder.append("). filter(?time < ");
		queryBuilder.append(getRDFTimeString(_end));
		queryBuilder.append("). filter( ?room = <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append(_room);
		queryBuilder.append(">)}");
		
		return queryBuilder.toString();
	}
	
	public static String getNotificationQuery()
	{
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select ?s ?o where{?s <");
		queryBuilder.append(PREFIXES.get(DEFAULT_PREFIX_KEY));
		queryBuilder.append("alertFromComputer> ?o}");
		Log.i(TAG, queryBuilder.toString());
		return queryBuilder.toString();
	}
	
	private static String getRDFTimeString(Date _d)
	{
		StringBuilder timeBuilder = new StringBuilder();
		timeBuilder.append("\"");
		timeBuilder.append(OPEN_RDF_DATE_FORMAT.format(_d));
		timeBuilder.append("\"^^<");
		timeBuilder.append(PREFIXES.get(XSD_PREFIX_KEY));
		timeBuilder.append("dateTime>");
		return timeBuilder.toString();
	}
	
}
