package services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.*;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.TupleValue;
import com.fasterxml.jackson.databind.node.ArrayNode;

import models.Attendee;
import models.AttendeeIn;
import models.Event;
import models.Feedback;
import models.FeedbackIn;
import models.Question;
import models.RoomAttendance;
import models.RoomLocation;
import models.User;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.Json;
import utils.DatabaseConnector;


public class DatabaseService {
	private final DatabaseConnector dbConnector;
	
	@Inject
	DatabaseService(ApplicationLifecycle lifecycle, DatabaseConnector connector) {
		this.dbConnector = connector;
		this.dbConnector.connect();
		
		lifecycle.addStopHook(() -> {
			dbConnector.stop();
            return CompletableFuture.completedFuture(null);
        });
	}

	public ArrayNode getAttendeesByEvent(final String eventID) {
		
		ResultSet results = dbConnector.selectAttendeeByEvent();
		List<Attendee> attendees = new ArrayList<Attendee>();
		
		for (Row row : results) {
			TupleValue tupleValue = row.getTupleValue("attendee_organization_level");
			
			Attendee attendee = new Attendee();
			attendee.setEventID(row.getString("event_id"));
			attendee.setEventName(row.getString("event_name"));
			attendee.setAttendeeID(row.getString("attendee_id"));
			attendee.setFirstName(row.getString("attendee_first_name"));
			attendee.setLastName(row.getString("attendee_last_name"));
			attendee.setEventAddressL1(row.getString("event_address_l1"));
			attendee.setEventAddressL2(row.getString("event_address_l2"));
			attendee.setEventCity(row.getString("event_city"));
			attendee.setEventState(row.getString("event_state"));
			attendee.setEventZip(row.getString("event_zip"));
			attendee.setEventCountry(row.getString("event_country"));
			attendee.setTimestamp(row.getString("registration_timestamp"));
			if (null != tupleValue) {
				attendee.setDivision(tupleValue.getString(0));
				attendee.setDepartment(tupleValue.getString(1));				
			}

			attendees.add(attendee);
		}
		return (ArrayNode) Json.toJson(attendees);
	}
	
	public ArrayNode getFeedbacksByEvent(final String eventID) {
		
		ResultSet results = dbConnector.selectFeedbacksByEvent(eventID);
		List<Feedback> feedbacks = new ArrayList<Feedback>();
		
		for (Row row : results) {
			
			Feedback feedback = new Feedback();
			feedback.setEventID(row.getString("event_id"));
			feedback.setEventName(row.getString("event_name"));
			feedback.setAttendeeID(row.getString("attendee_id"));
			feedback.setAttendeeFirstName(row.getString("attendee_first_name"));
			feedback.setAttendeeLastName(row.getString("attendee_last_name"));
			feedback.setAttendeeAddressL1(row.getString("attendee_address_l1"));
			feedback.setAttendeeAddressL2(row.getString("attendee_address_l2"));
			feedback.setAttendeeCity(row.getString("attendee_city"));
			feedback.setAttendeeState(row.getString("attendee_state"));
			feedback.setAttendeeZip(row.getString("attendee_zip"));
			feedback.setAttendeeCountry(row.getString("attendee_country"));
			feedback.setQuestionID(row.getString("question_id"));
			feedback.setQuestionText(row.getString("question_text"));
			feedback.setRatingAmount(row.getInt("rating_amount"));
			feedback.setRatingComment(row.getString("rating_comment"));
			feedback.setRatingDate(row.getString("rating_date"));

			feedbacks.add(feedback);
		}
		return (ArrayNode) Json.toJson(feedbacks);
	}	
	
	public RoomLocation getLocation(String beaconID) {
		return dbConnector.getLocation(beaconID);
	}

	public Event getEvent(String eventID) {
		return dbConnector.getEvent(eventID);
	}

	public Question getQuestion(String eventID, String questionID) {
		return dbConnector.getQuestion(eventID, questionID);
	}

	public ArrayNode getQuestionsByEvent(String eventID) {
		return dbConnector.getQuestionsByEvent(eventID);
	}
	
	public boolean saveAttendeeByEvent(AttendeeIn attendeeIn, Event event) {
		if (null == attendeeIn || null == event) {
			Logger.info("Attendee or event is null");
			return false;
		}
		Attendee attendee = new Attendee();		
		attendee.setEventID(attendeeIn.getEventID());
		attendee.setAttendeeID(attendeeIn.getAttendeeID());
		attendee.setEventName(event.getName());
		attendee.setEventAddressL1(event.getAddressL1());
		attendee.setEventAddressL2(event.getAddressL2());
		attendee.setEventCity(event.getCity());
		attendee.setEventState(event.getState());
		attendee.setEventZip(event.getZip());
		attendee.setEventCountry(event.getCountry());
		attendee.setTimestamp(attendeeIn.getTimestamp());
		
		attendee = dbConnector.setUserData(attendee);
			
		if (dbConnector.saveAttendeeByEvent(attendee)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean saveRoomAttendanceByEvent(final AttendeeIn attendeeIn, final String eventName, final RoomLocation room) {
		
		RoomAttendance roomAttendance = new RoomAttendance();		
		roomAttendance.setAttendeeID(attendeeIn.getAttendeeID());
		roomAttendance.setEventID(attendeeIn.getEventID());
		roomAttendance.setEventName(eventName);
		roomAttendance.setRoomName(room.getRoomName());
		roomAttendance.setRoomFloor(room.getRoomFloor());
		roomAttendance.setLocationName(room.getPlaceName());
		roomAttendance.setAddressL1(room.getAddressL1());
		roomAttendance.setAddressL2(room.getAddressL2());
		roomAttendance.setLocationCity(room.getCity());
		roomAttendance.setLocationState(room.getState());
		roomAttendance.setLocationZip(room.getZip());
		roomAttendance.setLocationCountry(room.getCountry());
		roomAttendance.setTimestamp(attendeeIn.getTimestamp());
		
		roomAttendance = dbConnector.setUserName(roomAttendance);
				
		if (dbConnector.saveRoomAttendance(roomAttendance)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean saveFeedback(FeedbackIn feedbackIn) {

		String beaconID = feedbackIn.getBeaconID();
		String eventID = feedbackIn.getEventID();
		String questionID = feedbackIn.getQuestionID();
		
		RoomLocation room = dbConnector.getLocation(beaconID);
		Event event = dbConnector.getEvent(eventID);
		User user = dbConnector.getUser(feedbackIn.getAttendeeID());
		Logger.info("feedbackIn.getAttendeeID()=" + feedbackIn.getAttendeeID());
		Question question = dbConnector.getQuestion(eventID, questionID);

		Feedback feedback = new Feedback();
		feedback.setAttendeeID(feedbackIn.getAttendeeID());
		feedback.setEventID(feedbackIn.getEventID());
		feedback.setQuestionID(questionID);
		feedback.setAttendeeAddressL1(room.getAddressL1());
		feedback.setAttendeeAddressL2(room.getAddressL2());
		feedback.setAttendeeCity(room.getCity());
		feedback.setAttendeeState(room.getState());
		feedback.setAttendeeZip(room.getZip());
		feedback.setAttendeeCountry(room.getCountry());
		feedback.setAttendeeFirstName(user.getFirstName());
		feedback.setAttendeeLastName(user.getLastName());
		feedback.setEventName(event.getName());
		feedback.setQuestionText(question.getQuestionText());
		feedback.setRatingAmount(feedbackIn.getRating());
		feedback.setRatingComment(feedbackIn.getComment());
		feedback.setRatingDate(feedbackIn.getTimestamp());
				
		if (dbConnector.saveFeedback(feedback)) {
			return true;
		} else {
			return false;
		}
		
	}	
}
