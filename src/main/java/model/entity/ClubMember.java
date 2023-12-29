package model.entity;

import java.util.Date;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class ClubMember {
	private Integer club_member_id;
	private String username;
	private String password;
	private String club_member_name;
	private String club_member_birth;
	private Integer club_member_booking_record_id;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
