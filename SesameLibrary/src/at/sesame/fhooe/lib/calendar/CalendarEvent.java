/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.calendar;

import java.util.Date;

import android.util.Log;

/**
 * this class represents a container for all information available for 
 * CalendarEvents on Android
 * @author Peter Riedl
 *
 */
public class CalendarEvent 
{
	//pre ICS
	private int mId;
	private String mSyncaccount;
	private String mSyncAccountType;
	private String mSyncId;
	private String mSyncVersion;
	private String mSyncTime;
	private int mSyncLocalId;
	private int mSyncDirty;
	private int mCalendarId;
	private String mHtmlUri;
	private String mTitle;
	private String mEventLocation;
	private String mDescription;
	private int mEventStatus;
	private int mSelfAttendeeStatus;
	private String mCommentsUri;
	private long mDtstart;
	private long mDtend;
	private String mEventTimeZone;
	private String mDuration;
	private int mAllDay;
	private int mVisibility;
	private int mTransparency;
	private int mHasAlarm;
	private int mHasExtendedProperties;
	private String mRRule;
	private String mExrule;
	private String mExdate;
	private String mOriginalEvent;
	private int mOriginalInstanceTime;
	private int mOriginalAllDay;
	private int mLastDate;
	private int mHasAttendeeData;
	private int mGuestsCanModify;
	private int mGuestsCanInviteOthers;
	private int mGuestsCanSeeGuests;
	private String mOrganizer;
	private int mDeleted;
	private long mBegin;
	private long mEnd;
	
	//ICS specifics
	private int mAccessLevel;
	private int mAvailability;
	private int mCanInviteOthers;
	private int mEventColor;
	private String mEventEndTimezone;
	private int mLastSynced;
	private String mOriginalSyncId;
	private String mRDate;
	private String mStatus;
	
	
	public CalendarEvent()
	{
		
	}
	
	/**
	 * creates a new CalendarEvent with all information present
	 */
	public CalendarEvent(int _id, String _syncaccount, String _syncAccountType,
			String _syncId, String _syncVersion, String _syncTime,
			int _syncLocalId, int _syncDirty, int _calendarId, String _htmlUri,
			String _title, String _eventLocation, String _description,
			int _eventStatus, int _selfAttendeeStatus, String _commentsUri,
			int _dtstart, int _dtend, String _eventTimeZone, String _duration,
			int _allDay, int _visibility, int _transparency, int _hasAlarm,
			int _hasExtendedProperties, String _rrule, String _exrule,
			String _exdate, String _originalEvent, int _originalInstanceTime,
			int _originalAllDay, int _lastDate, int _hasAttendeeData,
			int _guestsCanModify, int _guestsCanInviteOthers,
			int _guestsCanSeeGuests, String _organizer, int _deleted, long _begin, long _end) {
		super();
		this.mId = _id;
		this.mSyncaccount = _syncaccount;
		this.mSyncAccountType = _syncAccountType;
		this.mSyncId = _syncId;
		this.mSyncVersion = _syncVersion;
		this.mSyncTime = _syncTime;
		this.mSyncLocalId = _syncLocalId;
		this.mSyncDirty = _syncDirty;
		this.mCalendarId = _calendarId;
		this.mHtmlUri = _htmlUri;
		this.mTitle = _title;
		this.mEventLocation = _eventLocation;
		this.mDescription = _description;
		this.mEventStatus = _eventStatus;
		this.mSelfAttendeeStatus = _selfAttendeeStatus;
		this.mCommentsUri = _commentsUri;
		this.mDtstart = _dtstart;
		this.mDtend = _dtend;
		this.mEventTimeZone = _eventTimeZone;
		this.mDuration = _duration;
		this.mAllDay = _allDay;
		this.mVisibility = _visibility;
		this.mTransparency = _transparency;
		this.mHasAlarm = _hasAlarm;
		this.mHasExtendedProperties = _hasExtendedProperties;
		this.mRRule = _rrule;
		this.mExrule = _exrule;
		this.mExdate = _exdate;
		this.mOriginalEvent = _originalEvent;
		this.mOriginalInstanceTime = _originalInstanceTime;
		this.mOriginalAllDay = _originalAllDay;
		this.mLastDate = _lastDate;
		this.mHasAttendeeData = _hasAttendeeData;
		this.mGuestsCanModify = _guestsCanModify;
		this.mGuestsCanInviteOthers = _guestsCanInviteOthers;
		this.mGuestsCanSeeGuests = _guestsCanSeeGuests;
		this.mOrganizer = _organizer;
		this.mDeleted = _deleted;
		this.mBegin = _begin;
		this.mEnd = _end;
	}

	public int getId() {
		return mId;
	}

	public String getSyncaccount() {
		return mSyncaccount;
	}

	public String getSyncAccountType() {
		return mSyncAccountType;
	}

	public String getSyncId() {
		return mSyncId;
	}

	public String getSyncVersion() {
		return mSyncVersion;
	}

	public String getSyncTime() {
		return mSyncTime;
	}

	public int getSyncLocalId() {
		return mSyncLocalId;
	}

	public int getSyncDirty() {
		return mSyncDirty;
	}

	public int getCalendarId() {
		return mCalendarId;
	}

	public String getHtmlUri() {
		return mHtmlUri;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getEventLocation() {
		return mEventLocation;
	}

	public String getDescription() {
		return mDescription;
	}

	public int getEventStatus() {
		return mEventStatus;
	}

	public int getSelfAttendeeStatus() {
		return mSelfAttendeeStatus;
	}

	public String getCommentsUri() {
		return mCommentsUri;
	}

	public long getDtstart() {
		return mDtstart;
	}
	
	public Date getStartDateHC()
	{
		return new Date(mBegin);
	}

	public long getDtend() {
		return mDtend;
	}
	
	public Date getEndDateHC()
	{
		return new Date(mEnd);
	}

	public String getEventTimeZone() {
		return mEventTimeZone;
	}

	public String getDuration() {
		return mDuration;
	}

	public int getAllDay() {
		return mAllDay;
	}
	
	public boolean isAllDay()
	{
		return mAllDay==1;
	}

	public int getVisibility() {
		return mVisibility;
	}

	public int getTransparency() {
		return mTransparency;
	}

	public int getHasAlarm() {
		return mHasAlarm;
	}

	public int getHasExtendedProperties() {
		return mHasExtendedProperties;
	}

	public String getRRule() {
		return mRRule;
	}

	public String getExrule() {
		return mExrule;
	}

	public String getExdate() {
		return mExdate;
	}

	public String getOriginalEvent() {
		return mOriginalEvent;
	}

	public int getOriginalInstanceTime() {
		return mOriginalInstanceTime;
	}

	public int getOriginalAllDay() {
		return mOriginalAllDay;
	}

	public int getLastDate() {
		return mLastDate;
	}

	public int getHasAttendeeData() {
		return mHasAttendeeData;
	}

	public int getGuestsCanModify() {
		return mGuestsCanModify;
	}

	public int getGuestsCanInviteOthers() {
		return mGuestsCanInviteOthers;
	}

	public int getGuestsCanSeeGuests() {
		return mGuestsCanSeeGuests;
	}

	public String getOrganizer() {
		return mOrganizer;
	}

	public int getDeleted() {
		return mDeleted;
	}
	
	public int getAccessLevel()
	{
		return mAccessLevel;
	}
	
	public int getAvailability()
	{
		return mAvailability;
	}
	
	public int getCanInviteOthers()
	{
		return mCanInviteOthers;
	}
	
	public int getEventColor()
	{
		return mEventColor;
	}
	
	public String getEventEndTimezone()
	{
		return mEventEndTimezone;
	}
	
	public int getLastSynced()
	{
		return mLastSynced;
	}
	
	public String getOriginalSyncId()
	{
		return mOriginalSyncId;
	}
	
	public String getRDate()
	{
		return mRDate;
	}
	
	public String getStatus()
	{
		return mStatus;
	}
	
	public Date getStartDateICS()
	{
		return new Date(getDtstart());
	}
	
	public Date getEndDateICS()
	{
		return new Date(getDtend());
	}
	
	
	//***********************************************
	
	public void setId(int _id) {
		this.mId = _id;
	}

	public void setSyncaccount(String _syncaccount) {
		this.mSyncaccount = _syncaccount;
	}

	public void setSyncAccountType(String _syncAccountType) {
		this.mSyncAccountType = _syncAccountType;
	}

	public void setSyncId(String _syncId) {
		this.mSyncId = _syncId;
	}

	public void setSyncVersion(String _syncVersion) {
		this.mSyncVersion = _syncVersion;
	}

	public void setSyncTime(String _syncTime) {
		this.mSyncTime = _syncTime;
	}

	public void setSyncLocalId(int _syncLocalId) {
		this.mSyncLocalId = _syncLocalId;
	}

	public void setSyncDirty(int _syncDirty) {
		this.mSyncDirty = _syncDirty;
	}

	public void setCalendarId(int _calendarId) {
		this.mCalendarId = _calendarId;
	}

	public void setHtmlUri(String _htmlUri) {
		this.mHtmlUri = _htmlUri;
	}

	public void setTitle(String _title) {
		this.mTitle = _title;
	}

	public void setEventLocation(String _eventLocation) {
		this.mEventLocation = _eventLocation;
	}

	public void setDescription(String _description) {
		this.mDescription = _description;
	}

	public void setEventStatus(int _eventStatus) {
		this.mEventStatus = _eventStatus;
	}

	public void setSelfAttendeeStatus(int _selfAttendeeStatus) {
		this.mSelfAttendeeStatus = _selfAttendeeStatus;
	}

	public void setCommentsUri(String _commentsUri) {
		this.mCommentsUri = _commentsUri;
	}

	public void setDtstart(long _dtstart) {
		Log.e("CalendarEvent","dtstart = "+_dtstart);
		this.mDtstart = _dtstart;
	}

	public void setDtend(long _dtend) {
		this.mDtend = _dtend;
	}

	public void setEventTimeZone(String _eventTimeZone) {
		this.mEventTimeZone = _eventTimeZone;
	}

	public void setDuration(String _duration) {
		this.mDuration = _duration;
	}

	public void setAllDay(int _allDay) {
		this.mAllDay = _allDay;
	}

	public void setVisibility(int _visibility) {
		this.mVisibility = _visibility;
	}

	public void setTransparency(int _transparency) {
		this.mTransparency = _transparency;
	}

	public void setHasAlarm(int _hasAlarm) {
		this.mHasAlarm = _hasAlarm;
	}

	public void setHasExtendedProperties(int _hasExtendedProperties) {
		this.mHasExtendedProperties = _hasExtendedProperties;
	}

	public void setRRule(String _rRule) {
		this.mRRule = _rRule;
	}

	public void setExrule(String _exrule) {
		this.mExrule = _exrule;
	}

	public void setExdate(String _exdate) {
		this.mExdate = _exdate;
	}

	public void setOriginalEvent(String _originalEvent) {
		this.mOriginalEvent = _originalEvent;
	}

	public void setOriginalInstanceTime(int _originalInstanceTime) {
		this.mOriginalInstanceTime = _originalInstanceTime;
	}

	public void setOriginalAllDay(int _originalAllDay) {
		this.mOriginalAllDay = _originalAllDay;
	}

	public void setLastDate(int _lastDate) {
		this.mLastDate = _lastDate;
	}

	public void setHasAttendeeData(int _hasAttendeeData) {
		this.mHasAttendeeData = _hasAttendeeData;
	}

	public void setGuestsCanModify(int _guestsCanModify) {
		this.mGuestsCanModify = _guestsCanModify;
	}

	public void setGuestsCanInviteOthers(int _guestsCanInviteOthers) {
		this.mGuestsCanInviteOthers = _guestsCanInviteOthers;
	}

	public void setGuestsCanSeeGuests(int _guestsCanSeeGuests) {
		this.mGuestsCanSeeGuests = _guestsCanSeeGuests;
	}

	public void setOrganizer(String _organizer) {
		this.mOrganizer = _organizer;
	}

	public void setDeleted(int _deleted) {
		this.mDeleted = _deleted;
	}

	public void setBegin(long _begin) {
		this.mBegin = _begin;
	}

	public void setEnd(long _end) {
		this.mEnd = _end;
	}
	
	public void setAccessLevel(int _accessLevel)
	{
		this.mAccessLevel = _accessLevel;
	}
	
	public void setAvaialbility(int _availability)
	{
		this.mAvailability = _availability;
	}
	
	public void setCanInviteOthers(int _canInviteOthers)
	{
		this.mCanInviteOthers = _canInviteOthers;
	}
	
	public void setEventColor(int _evtCol)
	{
		this.mEventColor = _evtCol;
	}
	
	public void setEventEndTimezone(String _evtEndTimezone)
	{
		this.mEventEndTimezone = _evtEndTimezone;
	}
	
	public void setLastSynced(int _lastSynced)
	{
		this.mLastSynced = _lastSynced;
	}
	
	public void setOriginalSyncId(String _originalSyncId)
	{
		this.mOriginalSyncId = _originalSyncId;
	}
	
	public void setRDate(String _rDate)
	{
		this.mRDate = _rDate;
	}
	
	public void setStatus(String _status)
	{
		this.mStatus = _status;
	}
	

	@Override
	public String toString() {
		return "CalendarEvent [mId=" + mId + ", mCalendarId=" + mCalendarId
				+ ", mTitle=" + mTitle + ", mEventLocation=" + mEventLocation
				+ ", mDescription=" + mDescription + ", mEventStatus="
				+ mEventStatus + ", mDtstart=" + mDtstart + ", mDtend="
				+ mDtend + ", mEventTimeZone=" + mEventTimeZone
				+ ", mDuration=" + mDuration + ", mAllDay=" + mAllDay
				+ ", mOrganizer=" + mOrganizer + "]";
	}

	public String toLongString() {
		return "CalendarEvent [mId=" + mId + ", mSyncaccount=" + mSyncaccount
				+ ", mSyncAccountType=" + mSyncAccountType + ", mSyncId="
				+ mSyncId + ", mSyncVersion=" + mSyncVersion + ", mSyncTime="
				+ mSyncTime + ", mSyncLocalId=" + mSyncLocalId
				+ ", mSyncDirty=" + mSyncDirty + ", mCalendarId=" + mCalendarId
				+ ", mHtmlUri=" + mHtmlUri + ", mTitle=" + mTitle
				+ ", mEventLocation=" + mEventLocation + ", mDescription="
				+ mDescription + ", mEventStatus=" + mEventStatus
				+ ", mSelfAttendeeStatus=" + mSelfAttendeeStatus
				+ ", mCommentsUri=" + mCommentsUri + ", mDtstart=" + mDtstart
				+ ", mDtend=" + mDtend + ", mEventTimeZone=" + mEventTimeZone
				+ ", mDuration=" + mDuration + ", mAllDay=" + mAllDay
				+ ", mVisibility=" + mVisibility + ", mTransparency="
				+ mTransparency + ", mHasAlarm=" + mHasAlarm
				+ ", mHasExtendedProperties=" + mHasExtendedProperties
				+ ", mRrule=" + mRRule + ", mExrule=" + mExrule + ", mExdate="
				+ mExdate + ", mOriginalEvent=" + mOriginalEvent
				+ ", mOriginalInstanceTime=" + mOriginalInstanceTime
				+ ", mOriginalAllDay=" + mOriginalAllDay + ", mLastDate="
				+ mLastDate + ", mHasAttendeeData=" + mHasAttendeeData
				+ ", mGuestsCanModify=" + mGuestsCanModify
				+ ", mGuestsCanInviteOthers=" + mGuestsCanInviteOthers
				+ ", mGuestsCanSeeGuests=" + mGuestsCanSeeGuests
				+ ", mOrganizer=" + mOrganizer + ", mDeleted=" + mDeleted + "]";
	}
	
	
}
