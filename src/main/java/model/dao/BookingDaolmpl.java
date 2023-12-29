package model.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import model.entity.Booking;
import model.entity.ClubMember;
import model.entity.Court;

public class BookingDaolmpl implements BookingDao{
	
private static BookingDao _instance = new BookingDaolmpl();
	
	private JdbcTemplate jdbcTemplate;
	
	private BookingDaolmpl() {
		
		try {
	        // 透過 JNDI 來查找資源
	        InitialContext ctx = new InitialContext();
	        DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/practice"); // 使用 JNDI 名稱

	        // 設定 JdbcTemplate 的數據源
	        this.jdbcTemplate = new JdbcTemplate(ds);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static BookingDao getInstance() {
		return _instance;
	}

	@Override
	public int addBookingCourt(Booking booking) {
		String sql = "insert into booking(court_id, club_member_id, use_date)values(?, ?, ?)";
	    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

	    jdbcTemplate.update(conn -> {
	        PreparedStatement pstmt = conn.prepareStatement(sql, new String[] {"booking_id"});
	        pstmt.setInt(1, booking.getCourt_id());
	        pstmt.setInt(2, booking.getClub_member_id());
	        pstmt.setString(3, booking.getUse_date());
	        return pstmt;
	    }, keyHolder);

	    return keyHolder.getKey() != null ? keyHolder.getKey().intValue(): 0;
	}

	@Override
	public int cancelBookingCourtById(Integer bookingId) {
		String sql = "delete from booking where booking_id = ?";
		return jdbcTemplate.update(sql, bookingId);
	}

	@Override
	public int updateBookingCourtDateById(Integer bookingId, String newBookingDate) {
		String sql = "update booking set use_date = ? where booking_id = ?";
		return jdbcTemplate.update(sql, newBookingDate, bookingId);
	}

	@Override
	public List<Booking> findAllBooking() {
		//String sql = "select booking_id, court_id, club_member_id, use_date, createDate from booking";
		//return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Booking.class));
		
		String sql = "select "
				+ "booking.booking_id, booking.court_id, booking.club_member_id, booking.use_date, booking.createDate, "
				+ "court.court_id as court_court_id, court.court_name as court_court_name, "
				+ "club_member.club_member_id as club_member_id, club_member.club_member_name as clubMember_club_member_name "
				+ "from booking "
				+ "left join club_member on booking.club_member_id = club_member.club_member_id "
				+ "left join court on booking.court_id = court.court_id";
		
		ResultSetExtractor<List<Booking>> resultSetExtractor = JdbcTemplateMapperFactory.newInstance()
						.addKeys("booking_id")
						.newResultSetExtractor(Booking.class);
		
		List<Booking> bookings = jdbcTemplate.query(sql, resultSetExtractor);
		
		return bookings;
	}

	@Override
	public Optional<Booking> getBookingCourtById(Integer bookingId) {
		String sql = "select booking_id, court_id, club_member_id, use_date, createDate from booking where booking_id = ?";
		Booking booking = null;
		try {
			booking = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Booking.class), bookingId);
			
			if(booking != null) {
				Optional<Court> courtOpt = getCourtById(booking.getCourt_id());
				Optional<ClubMember> clubMemberOpt = getClubMemberById(booking.getClub_member_id());
				if(courtOpt.isPresent() && clubMemberOpt.isPresent()) {
					booking.setCourt(courtOpt.get()); // 注入 court 物件
					booking.setClubMember(clubMemberOpt.get()); // 注入 member 物件
				}
				return Optional.ofNullable(booking);
			}
			return Optional.empty();
		} catch (Exception e) {
		}
		return Optional.ofNullable(booking);
	}

	@Override
	public List<Court> findAllCourts() {
		String sql = "select court_id, court_name from court";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Court.class));
	}

	@Override
	public Optional<Court> getCourtById(Integer courtId) {
		String sql = "select court_id, court_name from court where court_id = ?";
		Court court = null;
		try {
			court = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Court.class), courtId);
			return Optional.ofNullable(court);
		} catch (Exception e) {
		}
		return Optional.ofNullable(court);
	}

	@Override
	public Booking getBookingIdByMemberId(Integer clubMemberId) {
		ClubMember clubMember = getClubMemberById(clubMemberId).get();
		Optional<Booking> bookingOpt = getBookingCourtById(clubMember.getClub_member_booking_record_id());
		return bookingOpt.get();
	}

	@Override
	public Optional<ClubMember> getClubMemberById(Integer clubMemberId) {
		String sql = "select club_member_id, club_member_username, "
				+ "club_member_password, club_member_name, club_member_birth, club_member_booking_record_id from club_member where club_member_id = ?";
		ClubMember clubMember = null;
		try {
			clubMember = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ClubMember.class), clubMemberId);
			return Optional.ofNullable(clubMember);
		} catch (Exception e) {
		}
		return Optional.ofNullable(clubMember);
	}

	@Override
	public List<ClubMember> findAllMembers() {
		String sql = "select club_member_id, club_member_username, club_member_name, "
				+ "club_member_birth, club_member_booking_record_id from club_member ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ClubMember.class));
	}

	@Override
	public int updateClubMemberBookingRecord(Integer clubMemderId, Integer bookingId) {
		String sql = "update club_member set club_member_booking_record_id = ? where club_member_id = ?";
		return jdbcTemplate.update(sql, bookingId, clubMemderId);
	}

}
