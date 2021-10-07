package item.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import item.model.Item;
import jdbc.JdbcUtil;

public class ItemDao {
	public Item insert(Connection conn, Item item) throws SQLException {
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("insert into item"
					+ " (comp_cd, plant_cd, acct_id, item_cd, item_nm, item_spec, item_spec2, item_color, cust_cd, acct_price, currency,"
					+ " unit_cd, remark, in_usr_id, in_date)"
					+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setInt(1, item.getComp_cd());
			pstmt.setInt(2, item.getPlant_cd());
			pstmt.setString(3, item.getAcct_id());
			pstmt.setInt(4, item.getItem_cd());
			pstmt.setString(5, item.getItem_nm());
			pstmt.setString(6, item.getItem_spec());
			pstmt.setString(7, item.getItem_spec2());
			pstmt.setString(8, item.getItem_color());
			pstmt.setString(9, item.getCust_cd());
			pstmt.setInt(10, item.getAcct_price());
			pstmt.setString(11, item.getCurrency());
			pstmt.setString(12, item.getUnit_cd());
			pstmt.setString(13, item.getRemark());
			pstmt.setString(14, item.getIn_usr_id());
			pstmt.setTimestamp(15, toTimestamp(item.getIn_date()));
			int insertedCount = pstmt.executeUpdate();
			
			if(insertedCount > 0) {
				return item;
			}
			else {
				System.out.println("데이터 입력 실패");
				return null;
			}
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(stmt);
		}
	}
	
	private Timestamp toTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}

	/* DB에 담긴 데이터 수 조회 */
	public int selectCount(Connection conn) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) from item");
			if(rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}

	/* DB전체 조회 후, List에 담기 */
	public List<Item> select (Connection conn, int startRow, int size) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from (select row_number() over (order by item_cd desc) num, i.* from item i order by item_cd desc) "
					+ "where num between ? and ?");
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, size);
			rs = pstmt.executeQuery();
			List<Item> result = new ArrayList<Item>();
			while(rs.next()) {
				result.add(convertItem(rs));
			}
			return result;
		}
		finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	public Item selectByNo(Connection conn, int item_cd) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from item where item_cd = ?");
			pstmt.setInt(1, item_cd);
			rs = pstmt.executeQuery();
			Item item = new Item();
			while (rs.next()) {
				item = convertItem(rs);
			}
			return item;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/* DB에서 조회한 내용을 model 객체로 변환 */
	private Item convertItem(ResultSet rs) throws SQLException{
		return new Item(rs.getInt("comp_cd"),
				rs.getInt("plant_cd"),
				rs.getString("acct_id"),
				rs.getInt("item_cd"),
				rs.getString("item_nm"),
				rs.getString("item_spec"),
				rs.getString("item_spec2"),
				rs.getString("item_color"),
				rs.getString("cust_cd"),
				rs.getInt("acct_price"),
				rs.getString("currency"),
				rs.getString("unit_cd"),
				rs.getString("remark"),
				rs.getString("in_usr_id"),
				rs.getDate("in_date"),
				rs.getString("up_usr_id"),
				rs.getDate("up_date")
				);
			
	}
	
	/* 수정 기능 */
	public int update(Connection conn, Integer item_cd, String acct_id, String item_nm, String item_spec, String item_spec2,
			String item_color, Integer acct_price, String currency, String unit_cd, String remark, String up_usr_id,
			Date up_date) throws SQLException {
		try (PreparedStatement pstmt = conn
				.prepareStatement("update item set acct_id = ?, item_nm = ?, item_spec = ?, item_spec2 = ?, "
						+ "item_color = ?, acct_price = ?, currency = ?, unit_cd = ?, remark = ?, up_usr_id = ?, "
						+ "up_date = ? where item_cd = ?")) {
			pstmt.setString(1, acct_id);
			pstmt.setString(2, item_nm);
			pstmt.setString(3, item_spec);
			pstmt.setString(4, item_spec2);
			pstmt.setString(5, item_color);
			pstmt.setInt(6, acct_price);
			pstmt.setString(7, currency);
			pstmt.setString(8, unit_cd);
			pstmt.setString(9, remark);
			pstmt.setString(10, up_usr_id);
			pstmt.setTimestamp(11, toTimestamp(up_date));
			pstmt.setInt(12, item_cd);
			return pstmt.executeUpdate();
		}
	}
	/* 삭제 기능 */
	public int delete(Connection conn, int item_cd) throws SQLException {
		try (PreparedStatement pstmt = conn
				.prepareStatement("delete from item where item_cd = ?")) {
			pstmt.setInt(1, item_cd);
			return pstmt.executeUpdate();
		}
	}

}
