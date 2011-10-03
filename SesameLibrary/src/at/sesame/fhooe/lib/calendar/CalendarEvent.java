/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.calendar;

import java.util.Date;

/**
 * this class represents a container for all information available for 
 * CalendarEvents on Android
 * @author Peter Riedl
 *
 */
public class CalendarEvent 
{
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
	private int mDtstart;
	private int mDtend;
	private String mEventTimeZone;
	private String mDuration;
	private int mAllDay;
	private int mVisibility;
	private int mTransparency;
	private int mHasAlarm;
	private int mHasExtendedProperties;
	private String mRrule;
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
		this.mRrule = _rrule;
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

	public int getDtstart() {
		return mDtstart;
	}
	
	public Date getBeginDate()
	{
		return new Date(mBegin);
	}

	public int getDtend() {
		return mDtend;
	}
	
	public Date getEndDate()
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

	public String getRrule() {
		return mRrule;
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
				+ ", mRrule=" + mRrule + ", mExrule=" + mExrule + ", mExdate="
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
