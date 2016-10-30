package controllers;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

import models.AttendeeIn;
import models.Event;
import models.FeedbackIn;
import models.RoomLocation;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.DatabaseService;

public class BeaconController extends Controller {
	private final DatabaseService dbService;
	
	@Inject
	public BeaconController(DatabaseService dbService) {
		this.dbService = dbService;
	}

    public Result getQuestionsByEvent(String eventID) {
		Logger.info("Get question list");
		return ok(dbService.getQuestionsByEvent(eventID));		
    }
    
    public Result getAttendeesByEvent(String eventID) {
		Logger.info("Get attendance list");
		return ok(dbService.getAttendeesByEvent(eventID));		
    }

    public Result getFeedbacksByEvent(String eventID) {
		Logger.info("Get feedback list");
		return ok(dbService.getFeedbacksByEvent(eventID));		
    }

    public Result postAttendee() {
		Logger.info("Get attendee");
		AttendeeIn attendeeIn = Json.fromJson(request().body().asJson(), AttendeeIn.class);
		String beaconID = attendeeIn.getBeaconID();
		String eventID = attendeeIn.getEventID();
		RoomLocation room = dbService.getLocation(beaconID);
		
		if (StringUtils.isEmpty(beaconID) || StringUtils.isEmpty(eventID) || null == room) {
			return badRequest(Json.newObject().put("response", "error"));			
		}
		
		Event event = dbService.getEvent(eventID);
		
		if ( dbService.saveAttendeeByEvent(attendeeIn, event) && dbService.saveRoomAttendanceByEvent(attendeeIn, event.getName(), room)) {
			return ok(Json.newObject().put("response", "success"));	
		}
		else {
			return badRequest(Json.newObject().put("response", "error"));
		}
    }
    
    public Result postFeedback() {
		FeedbackIn feedbackIn = Json.fromJson(request().body().asJson(), FeedbackIn.class);
		
		if (dbService.saveFeedback(feedbackIn)) {
			return ok(Json.newObject().put("response", "success"));	
		}
		else {
			return badRequest(Json.newObject().put("response", "error"));
		}
    }

}
