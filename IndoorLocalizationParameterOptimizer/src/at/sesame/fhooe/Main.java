package at.sesame.fhooe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import at.sesame.fhooe.classification.Optimizer;
import at.sesame.fhooe.fingerprintInformation.FPIParser;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		
		try {
			FPIParser fpip = new FPIParser();
			fpip.parse(new FileInputStream("res/MP_lookup/fpi.xml"));
			System.out.println("number of rooms:"+fpip.getRooms().size());
			System.out.println("number of fingerprint items:"+fpip.getFingerPrintItems().size());
			new Optimizer(fpip.getRooms(),fpip.getFingerPrintItems());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
