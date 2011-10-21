/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.errorhandling.handlers;


/**
 * handles errors that occur during an poweroff call
 *
 */
public class PowerOffErrorHandler 
extends PMSErrorHandler 
{
	@Override
	protected void handleHttpError(StringBuilder _msg, int _code) 
	{
		switch(_code)
		{
		case 400:
			_msg.append(PMSErrorMessage400);
			break;
		case 401:
			_msg.append(PMSErrorMessage401);
			break;
		case 404:
			_msg.append(PMSErrorMessage404); 
			break;
		case 406:
			_msg.append(PMSErrorMessage406);
			break;
		case 410:
			_msg.append(PMSErrorMessage410);
			break;
		case 500:
			_msg.append(PMSErrorMessage500);
			break;
		case 501:
			_msg.append(PMSErrorMessage501);
			break;
		default:
			_msg.append(PMSErrorMessageCodeNotRecognized);
			break;
		}
	}

}
