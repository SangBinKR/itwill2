package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import jdk.nashorn.internal.ir.RuntimeNode.Request;
import vo.MemberBean;

//import db.JdbcUtil;
import static db.JdbcUtil.*;


public class MemberDAO {
	private MemberDAO() {}
	
	private static MemberDAO instance = new MemberDAO();

	public static MemberDAO getInstance() {
		return instance;
	}
	// ========================================================================

	Connection con; 

	public void setConnection(Connection con) {
		this.con = con;
	}

	public int insertArticle(MemberBean memberBean) {
		System.out.println("MemberDAO - insertArticle()");
		int insertCount = 0; 
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int num = 1; 
		
		try {
			String sql = "SELECT MAX(num) FROM member";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				num = rs.getInt(1) + 1; 
			} 
			
			sql = "INSERT INTO member VALUES (?,?,?,?,?,?,?,?,?,now())";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num); 
			pstmt.setString(2,memberBean.getId());
			pstmt.setString(3, memberBean.getName());
			pstmt.setString(4, memberBean.getPassword());
			pstmt.setString(5, memberBean.getEmail());
			pstmt.setString(6, memberBean.getPhone());
			pstmt.setString(7, memberBean.getCatg()); // ������ ��ȣ(�� ���̹Ƿ� �ڽ��� �������� ��)
			pstmt.setInt(8, memberBean.getAge());
			pstmt.setString(9, memberBean.getAddress());
			
			insertCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("insertArticle() ����! - " + e.getMessage());
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return insertCount;
	}

	public int selectListCount() {
		int listCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COUNT(board_num) FROM member";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				listCount = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			System.out.println("selectListCount() ����! - " + e.getMessage());
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return listCount;
	}

	public ArrayList<MemberBean> selectArticleList(int page, int limit) {
		ArrayList<MemberBean> articleList = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int startRow = (page - 1) * limit;
		
		try {
			String sql = "SELECT * FROM member LIMIT ?,?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, limit);
			rs = pstmt.executeQuery();
			
			articleList = new ArrayList<MemberBean>();
			
			while(rs.next()) {
				MemberBean article = new MemberBean();
				article.setNum(rs.getInt("num"));
				article.setName(rs.getString("name"));
				article.setId(rs.getString("id"));
				article.setPassword(rs.getString("password"));
				article.setEmail(rs.getString("email"));
				article.setPhone(rs.getString("phone"));
				article.setCatg(rs.getString("catg"));
				article.setAge(rs.getInt("age"));
				article.setAddress(rs.getString("address"));
				article.setDate(rs.getDate("date"));
				
				articleList.add(article);
			}
			
		} catch (SQLException e) {
			System.out.println("selectArticleList() ����! - " + e.getMessage());
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return articleList;
	}

	public MemberBean selectArticle(int id) {
		MemberBean article = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "select * from board where id=?";
		
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			
			// �Խù��� ������ ��� BoardBean ��ü�� �����Ͽ� �Խù� ���� ����
			
			if(rs.next()) {
				article = new MemberBean();
				article.setNum(rs.getInt("num"));
				article.setName(rs.getString("name"));
				article.setPassword(rs.getString("password"));
				article.setId(rs.getString("id"));
				article.setEmail(rs.getString("email"));
				article.setPhone(rs.getString("phone"));
				article.setCatg(rs.getString("catg"));
				article.setAge(rs.getInt("age"));
				article.setAddress(rs.getString("address"));
				article.setDate(rs.getDate("date"));
			}
		} catch (SQLException e) {
			System.out.println("selectArticle() ����! - " + e.getMessage());
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
		}
		
		System.out.println("���̵� : "+article.getId());
		
		
		return article;
	}

		public boolean isArticleMemberWriter(String id, String password) {
			boolean isArticleWriter = false;
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				String sql = "SELECT password FROM member WHERE id=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					if(password.equals(rs.getString("password"))) {
						isArticleWriter = true;
					}
				}
				
			} catch (SQLException e) {
				System.out.println("isArticleMemberWriter() ����! - " + e.getMessage());
				e.printStackTrace();
			} finally {
				close(rs);
				close(pstmt);
			}
			
			return isArticleWriter;
		}

		public int updateArticle(MemberBean article) {
			int updateCount = 0;
			
			PreparedStatement pstmt = null;
			try {
				String sql = "UPDATE board SET name=?, email=?, phone=?, catg=?, age=?, address=? WHERE id=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, article.getName());
				pstmt.setString(2, article.getEmail());
				pstmt.setString(3, article.getPhone());
				pstmt.setString(4, article.getCatg());
				pstmt.setInt(5, article.getAge());
				pstmt.setString(6, article.getAddress());
				pstmt.setString(7, article.getId());
				updateCount = pstmt.executeUpdate();
				
			} catch (SQLException e) {
				System.out.println("updateArticle() ����! - " + e.getMessage());
				e.printStackTrace();
			} finally {
				close(pstmt);
			}
			
			return updateCount;
		}
		
		public int deleteArticle(MemberBean article) {
			int updateCount = 0;
			
			PreparedStatement pstmt = null;
			try {
				String sql = "delete from member WHERE id=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, article.getId());
				updateCount = pstmt.executeUpdate();
				
			} catch (SQLException e) {
				System.out.println("deleteArticle() ����! - " + e.getMessage());
				e.printStackTrace();
			} finally {
				close(pstmt);
			}
			
		return updateCount;
	}
		
		
		
		
	}



















