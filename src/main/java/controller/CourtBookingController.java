package controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.BookingDao;
import model.dao.BookingDaolmpl;
import model.entity.Booking;
import model.entity.ClubMember;
import model.entity.Court;

@WebServlet(value = "/court/booking/*")
public class CourtBookingController extends HttpServlet{

	private BookingDao dao = BookingDaolmpl.getInstance();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if(pathInfo.contains("/bookingCourt")) {
			String regex = "^/bookingCourt/(\\d+)$";
			Integer id = getId(pathInfo, regex);
			if(id == null) {
				resp.getWriter().print(dao.findAllBooking());
			}else {
				Optional<Booking> bookingOpt = dao.getBookingCourtById(id);
				if(bookingOpt.isPresent()) {
					resp.getWriter().print(bookingOpt.get());
				}else {
					resp.getWriter().print("{}");
				}
			}
		}
		if(pathInfo.contains("/courts")) {
			String regex = "^/courts/(\\d+)$";
			Integer id = getId(pathInfo, regex);
			if(id == null) {
				resp.getWriter().print(dao.findAllCourts());
			}else {
				Optional<Court> courtOpt = dao.getCourtById(id);
				if(courtOpt.isPresent()) {
					resp.getWriter().print(courtOpt.get());
				}else {
					resp.getWriter().print("{}");
				}
			}
		}
		if(pathInfo.contains("/club_member")) {
			String regex = "^/club_member/(\\d+)$";
			Integer id = getId(pathInfo, regex);
			if(id == null) {
				resp.getWriter().print(dao.findAllMembers());
			}else {
				Optional<ClubMember> memberOpt = dao.getClubMemberById(id);
				if(memberOpt.isPresent()) {
					resp.getWriter().print(memberOpt.get());
				}else {
					resp.getWriter().print("{}");
				}
			}
		}
	}	

	/* 輸入:
	 {
		"court_id" : 3,
		"club_member_id" : 1,
		"use_date" : "2024-01-06"
	}
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Gson gson = new Gson();
		String pathInfo = req.getPathInfo();
		switch(pathInfo) {
			case "/bookingCourt":
				String bookingCourtJsonStr = req.getReader().lines().collect(Collectors.joining("\n"));
				// json str 轉 bean
				Booking booking = gson.fromJson(bookingCourtJsonStr, Booking.class);
				ClubMember clubMember = dao.getClubMemberById(booking.getClub_member_id()).get();
				if(clubMember.getClub_member_booking_record_id() == null) {
					try {
						int bookingId = dao.addBookingCourt(booking);
						int rowcount = dao.updateClubMemberBookingRecord(clubMember.getClub_member_id(), bookingId);
						resp.getWriter().print("{\"result\": \"OK\", \"bookingId\": " + bookingId + "}");
					}catch (Exception e) {
						resp.getWriter().print("{\"result\": \"Fail\", \"exception\": \"" + e.getMessage() + "\"}");
					}
				}else {
					resp.getWriter().print("{\"result\": \"Fail\", \"exception\": \"已預約場地\"}");
				}
				break;
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		int bookingId = getId(pathInfo, "^/bookingCourt/(\\d+)$");
		//resp.getWriter().print(bookingId);
		String jsonStr = req.getReader().lines().collect(Collectors.joining("\n"));
		Gson gson = new Gson();
		//Type mapType = new TypeToken<Map<String, String>>(){}.getType(); // 建立一個 Map<String,String> 的型別
		//Map<String, String> map = gson.fromJson(jsonStr, mapType);
		Map map = gson.fromJson(jsonStr, Map.class);
		String bookingDate = map.get("use_date") + "";
		try {
			int rowcount = dao.updateBookingCourtDateById(bookingId, bookingDate);
			if(rowcount == 0) {
				resp.getWriter().print("{\"result\": \"Fail\", \"bookingId\": " + bookingId + "}");
			}else {
				resp.getWriter().print("{\"result\": \"OK\", \"bookingId\": " + bookingId + "}");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		int bookingId = getId(pathInfo, "^/bookingCourt/(\\d+)$");
		int rowcount = dao.cancelBookingCourtById(bookingId);
		if(rowcount == 0) {
			resp.getWriter().print("{\"result\": \"Fail\", \"bookingId\": " + bookingId + "}");
		}else {
			resp.getWriter().print("{\"result\": \"OK\", \"bookingId\": " + bookingId + "}");
		}
	}
	
	public Integer getId(String pathInfo, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(pathInfo);
		if(matcher.find()) {
			String numberStr = matcher.group(1);
			return Integer.parseInt(numberStr);
		}
		return null;
	}
}
