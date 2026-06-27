package com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.dao.WatchlistDao;
import com.model.Watchlist;
import com.util.DBUtil;

public class WatchlistDaoImpl implements WatchlistDao{

	@Override
	public boolean addStock(
	        Long userId,
	        String stockCode,
	        String market) {

	    String sql =
	        "INSERT INTO watchlist(user_id,stock_code,market) VALUES(?,?,?)";

	    try (
	        Connection conn=DBUtil.getConnection();
	        PreparedStatement ps=conn.prepareStatement(sql)
	    ) {

	        ps.setLong(1, userId);
	        ps.setString(2, stockCode);
	        ps.setString(3, market);

	        ps.executeUpdate();
	        return true;

	    } catch (Exception e) {
	        // UNIQUE(user_id, stock_code) 衝突時會丟例外，代表已經追蹤過了
	        System.out.println("新增追蹤失敗（可能已存在）: " + e.getMessage());
	        return false;
	    }
	}

	@Override
	public List<Watchlist> findByUserId(Long userId) {

		String sql="SELECT * FROM watchlist WHERE user_id = ?";

		List<Watchlist> list = new ArrayList<>();

		try (
			Connection conn=DBUtil.getConnection();

			PreparedStatement ps=conn.prepareStatement(sql)
		) {

			ps.setLong(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Watchlist watchlist = new Watchlist();
				watchlist.setId(rs.getLong("id"));
				watchlist.setUserId(rs.getLong("user_id"));
				watchlist.setStockCode(rs.getString("stock_code"));
				watchlist.setMarket(rs.getString("market"));

				list.add(watchlist);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public void deleteStock(Long userId, String stockCode) {

		String sql="DELETE FROM watchlist WHERE user_id = ? AND stock_code = ?";

		try (
			Connection conn=DBUtil.getConnection();

			PreparedStatement ps=conn.prepareStatement(sql)
		) {

			ps.setLong(1, userId);
			ps.setString(2, stockCode);

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Watchlist> findAllDistinctStocks() {

		// 用 DISTINCT ON 取得每個 stock_code 唯一一筆（含 market），避免多使用者追蹤同一檔造成重複
		String sql="SELECT DISTINCT ON (stock_code) stock_code, market FROM watchlist";

		List<Watchlist> list = new ArrayList<>();

		try (
			Connection conn=DBUtil.getConnection();

			PreparedStatement ps=conn.prepareStatement(sql)
		) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Watchlist watchlist = new Watchlist();
				watchlist.setStockCode(rs.getString("stock_code"));
				watchlist.setMarket(rs.getString("market"));
				list.add(watchlist);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}