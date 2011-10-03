/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.location.geocoder;

/**
 * this class represents a container for all information retrieved by the yahoo
 * geocoding webservice 
 * @see http://developer.yahoo.com/geo/placefinder/guide/
 * @author admin
 *
 */
public class GeoCoderResult 
{
	private double mQuality = 0.0;
	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	private double mOffsetLat = 0.0;
	private double mOffsetLon = 0.0;
	private double mRadius = 0.0;
	private String mName = "";
	private String mLine1 = "";
	private String mLine2 = "";
	private String mLine3 = "";
	private String mLine4 = "";
	private String mHouse = "";
	private String mStreet = "";
	private String mXStreet = "";
	private String mUnittype = "";
	private String mUnit = "";
	private String mPostal = "";
	private String mNeighborhood = "";
	private String mCity = "";
	private String mCounty = "";
	private String mState = "";
	private String mCountry = "";
	private String mCountryCode = "";
	private String mStateCode = "";
	private String mCountyCode = "";
	private String mHash = "";
	private String mWoeid = "";
	private String mWoetype = "";
	private String mUzip = "";
	
	public GeoCoderResult(double quality, double latitude, double longitude,
			double offsetLat, double offsetLon, double radius, String name,
			String line1, String line2, String line3, String line4,
			String house, String street, String xstreet, String unittype,
			String unit, String postal, String neighborhood, String city, 
			String county,String state, String country, String countryCode, 
			String stateCode,String countyCode, String hash, String woeid, 
			String woetype,String uzip) {
		super();
		this.mQuality = quality;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mOffsetLat = offsetLat;
		this.mOffsetLon = offsetLon;
		this.mRadius = radius;
		this.mName = name;
		this.mLine1 = line1;
		this.mLine2 = line2;
		this.mLine3 = line3;
		this.mLine4 = line4;
		this.mHouse = house;
		this.mStreet = street;
		this.mXStreet = xstreet;
		this.mUnittype = unittype;
		this.mPostal = postal;
		this.mNeighborhood = neighborhood;
		this.mCity = city;
		this.mCounty = county;
		this.mState = state;
		this.mCountry = country;
		this.mCountryCode = countryCode;
		this.mStateCode = stateCode;
		this.mCountyCode = countyCode;
		this.mHash = hash;
		this.mWoeid = woeid;
		this.mWoetype = woetype;
		this.mUzip = uzip;
	}
	
	public GeoCoderResult()
	{
		
	}

	public double getQuality() {
		return mQuality;
	}

	public void setQuality(double quality) {
		this.mQuality = quality;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}

	public double getOffsetLat() {
		return mOffsetLat;
	}

	public void setOffsetLat(double offsetLat) {
		this.mOffsetLat = offsetLat;
	}

	public double getOffsetLon() {
		return mOffsetLon;
	}

	public void setOffsetLon(double offsetLon) {
		this.mOffsetLon = offsetLon;
	}

	public double getRadius() {
		return mRadius;
	}

	public void setRadius(double radius) {
		this.mRadius = radius;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getLine1() {
		return mLine1;
	}

	public void setLine1(String line1) {
		this.mLine1 = line1;
	}

	public String getLine2() {
		return mLine2;
	}

	public void setLine2(String line2) {
		this.mLine2 = line2;
	}

	public String getLine3() {
		return mLine3;
	}

	public void setLine3(String line3) {
		this.mLine3 = line3;
	}

	public String getLine4() {
		return mLine4;
	}

	public void setLine4(String line4) {
		this.mLine4 = line4;
	}

	public String getHouse() {
		return mHouse;
	}

	public void setHouse(String house) {
		this.mHouse = house;
	}

	public String getStreet() {
		return mStreet;
	}

	public void setStreet(String street) {
		this.mStreet = street;
	}

	public String getXstreet() {
		return mXStreet;
	}

	public void setXstreet(String xstreet) {
		this.mXStreet = xstreet;
	}

	public String getUnittype() {
		return mUnittype;
	}

	public void setUnittype(String unittype) {
		this.mUnittype = unittype;
	}

	public String getUnit() {
		return mUnit;
	}

	public void setUnit(String unit) {
		this.mUnit = unit;
	}

	public String getPostal() {
		return mPostal;
	}

	public void setPostal(String postal) {
		this.mPostal = postal;
	}

	public String getNeighborhood() {
		return mNeighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.mNeighborhood = neighborhood;
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String city) {
		this.mCity = city;
	}

	public String getCounty() {
		return mCounty;
	}

	public void setCounty(String county) {
		this.mCounty = county;
	}

	public String getState() {
		return mState;
	}

	public void setState(String state) {
		this.mState = state;
	}

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String country) {
		this.mCountry = country;
	}

	public String getCountryCode() {
		return mCountryCode;
	}

	public void setCountryCode(String countryCode) {
		this.mCountryCode = countryCode;
	}

	public String getStateCode() {
		return mStateCode;
	}

	public void setStateCode(String stateCode) {
		this.mStateCode = stateCode;
	}

	public String getCountyCode() {
		return mCountyCode;
	}

	public void setCountyCode(String countyCode) {
		this.mCountyCode = countyCode;
	}

	public String getHash() {
		return mHash;
	}

	public void setHash(String hash) {
		this.mHash = hash;
	}

	public String getWoeid() {
		return mWoeid;
	}

	public void setWoeid(String woeid) {
		this.mWoeid = woeid;
	}

	public String getWoetype() {
		return mWoetype;
	}

	public void setWoetype(String woetype) {
		this.mWoetype = woetype;
	}

	public String getUzip() {
		return mUzip;
	}

	public void setUzip(String uzip) {
		this.mUzip = uzip;
	}

	@Override
	public String toString() {
		return "GeoCoderResult [quality=" + mQuality + ", latitude=" + mLatitude
				+ ", longitude=" + mLongitude + ", offsetLat=" + mOffsetLat
				+ ", offsetLon=" + mOffsetLon + ", radius=" + mRadius + ", name="
				+ mName + ", line1=" + mLine1 + ", line2=" + mLine2 + ", line3="
				+ mLine3 + ", line4=" + mLine4 + ", house=" + mHouse + ", street="
				+ mStreet + ", xstreet=" + mXStreet + ", unittype=" + mUnittype
				+ ", unit=" + mUnit + ", postal=" + mPostal + ", neighborhood="
				+ mNeighborhood + ", city=" + mCity + ", county=" + mCounty
				+ ", state=" + mState + ", country=" + mCountry
				+ ", countryCode=" + mCountryCode + ", stateCode=" + mStateCode
				+ ", countyCode=" + mCountyCode + ", hash=" + mHash + ", woeid="
				+ mWoeid + ", woetype=" + mWoetype + ", uzip=" + mUzip + "]";
	}
	
	
	
}
