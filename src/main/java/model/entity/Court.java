package model.entity;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class Court {
	private Integer court_id;
	private String court_name;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
