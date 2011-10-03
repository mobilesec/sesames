package at.sesame.fhooe.classification;

import android.content.Context;
import at.sesame.fhooe.R;

public class ClassificatorException 
extends Exception 
{
	private static final long serialVersionUID = 1L;

	public enum ExceptionType
	{
		TRAINING_DATA_NOT_SET,
		CLASSIFIER_NOT_SET,
		NO_BSSID_RECOGNIZED
		
	}
	public ClassificatorException(Context _context, ExceptionType _type)
	{
		super(getExceptionTypeMessage(_context, _type));
		
	}
	
	private static String getExceptionTypeMessage(Context _context, ExceptionType _type)
	{
		switch (_type) {
		case TRAINING_DATA_NOT_SET:
			return _context.getString(R.string.classificatorException_trainingDataNotSetMesage);
		case CLASSIFIER_NOT_SET:
			return _context.getString(R.string.classificatorException_classifierNotSetMesage);
		case NO_BSSID_RECOGNIZED:
			return _context.getString(R.string.classificatorException_noBssidRecognizedMessage);
		default:
			return "ClassificationException says: impossible";
		}
	}
}
