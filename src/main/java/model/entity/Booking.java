package model.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class Booking {
	private Integer booking_id;
	private Integer court_id;
	private Integer club_member_id;
	private String use_date;
	private Timestamp createDate;
	
	private Court court;
	private ClubMember clubMember;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
