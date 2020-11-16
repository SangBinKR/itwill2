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
			
			// INSERT ���� ���� ������� int�� ���� insertCount �� ����
			insertCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("insertArticle() ����! - " + e.getMessage());
			e.printStackTrace();
		} finally {
			// �ڿ� ��ȯ
			// ����! DAO Ŭ���� ������ Connection ��ü ��ȯ ����!
			close(rs);
			close(pstmt);
		}
		
		return insertCount;
	}

	// ��ü �Խù� �� ��ȸ
	public int selectListCount() {
		int listCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// SELECT ������ ����Ͽ� ��ü �Խù� �� ��ȸ
			// => count() �Լ� ���, ��ȸ ��� �÷� 1�� �����ϰų� * ���
			String sql = "SELECT COUNT(board_num) FROM member";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			// ��ȸ ����� ���� ���(= �Խù��� �ϳ��� �����ϴ� ���)
			// => �Խù� ���� listCount �� ����
			if(rs.next()) {
				listCount = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			System.out.println("selectListCount() ����! - " + e.getMessage());
			e.printStackTrace();
		} finally {
			// �ڿ� ��ȯ
			// ����! DAO Ŭ���� ������ Connection ��ü ��ȯ ����!
			close(rs);
			close(pstmt);
		}
		
		return listCount;
	}

	// �Խù� ��� ��ȸ
	public ArrayList<MemberBean> selectArticleList(int page, int limit) {
		// ������ ������ŭ�� �Խù� ��ȸ �� ArrayList ��ü�� ������ �� ����
		ArrayList<MemberBean> articleList = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// ��ȸ�� ������ ���ڵ�(��) ��ȣ ���
		int startRow = (page - 1) * limit;
		
		try {
			// �Խù� ��ȸ
			// �����۹�ȣ(board_re_ref) ��ȣ�� �������� �������� ����,
			// ������ȣ(board_re_seq) ��ȣ�� �������� �������� ����
			// ��ȸ ���� �Խù� ��ȣ(startRow)���� limit ������ŭ ��ȸ
			String sql = "SELECT * FROM member LIMIT ?,?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, limit);
			rs = pstmt.executeQuery();
			
			// ArrayList ��ü ����(while�� ������ ���� �ʼ�!)
			articleList = new ArrayList<MemberBean>();
			
			// �о�� �Խù��� ������ ��� ���� �۾� �ݺ�
			// => BoardBean ��ü�� �����Ͽ� ���ڵ� ������ ��� ���� ��
			//    BoardBean ��ü�� �ٽ� ArrayList ��ü�� �߰�
			// => ��, �н�����(board_pass) �� ����
			while(rs.next()) {
				// 1�� �Խù� ������ ������ BoardBean ��ü ���� �� ������ ����
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
				
				// ���ڵ� ���� Ȯ�ο� �ڵ�
//				System.out.println("���� : " + article.getBoard_subject());
				
				// 1�� �Խù��� ��ü �Խù� ���� ��ü(ArrayList)�� �߰�
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

	// �Խù� �� ���� ��ȸ
	public MemberBean selectArticle(int board_num) {
		// �۹�ȣ(board_num)�� �ش��ϴ� ���ڵ带 SELECT
		// ��ȸ ����� ���� ��� BoardBean ��ü�� ������ �� ����
		MemberBean article = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "select * from board where board_num=?";
		
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
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
		
		
		// �ӽ� Ȯ�ο� �� ���� ���
		System.out.println("���̵� : "+article.getId());
		
		
		return article;
	}

	
}











