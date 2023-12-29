package model.dao;

import java.util.List;
import java.util.Optional;

import model.entity.Booking;
import model.entity.ClubMember;
import model.entity.Court;

public interface BookingDao {
	
	int addBookingCourt(Booking booking); // 新增預約
	
	int cancelBookingCourtById(Integer bookingId); // 取消預約
	
	int updateBookingCourtDateById(Integer bookingId, String newBookingDate); // 變更預約時間
	
	List<Booking> findAllBooking(); // 查詢所有預約狀態
	
	Optional<Booking> getBookingCourtById(Integer bookingId); // 取得單筆預約狀態
	
	List<Court> findAllCourts(); // 查詢所有球場
	
	Optional<Court> getCourtById(Integer courtId); // 取得單筆球場
	
	Optional<ClubMember> getClubMemberById(Integer clubMemberId); // 取得會員資料
	
	Booking getBookingIdByMemberId(Integer clubMemberId); // 取得會員預約的球場
	
	List<ClubMember> findAllMembers(); // 查詢會員清單
	
	int updateClubMemberBookingRecord(Integer clubMemderId, Integer bookingId);

}
