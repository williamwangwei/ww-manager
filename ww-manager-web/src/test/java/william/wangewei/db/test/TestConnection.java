package william.wangewei.db.test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import william.wang.db.util.DbUtil;
public class TestConnection {
	@Test
	public void testConnection(){
		//创建一个spring容器
				ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
				JdbcTemplate jdbcTemplate = (JdbcTemplate)ac.getBean("jdbcTemplate");
				String sql = "select * from dept";
				List<Map<String, Object>> resList = jdbcTemplate.query(sql, new ResultSetExtractor<List<Map<String,Object>>>(){

					@Override
					public List<Map<String,Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
						List<Map<String,Object>> resList =null;
						
						resList = new ArrayList<Map<String,Object>>();
						while(rs.next()){
							Map<String,Object> map = new HashMap<String,Object>();
							
							map.put("DNAME", rs.getString("DNAME"));
							map.put("LOC", rs.getString("LOC"));
							map.put("DEPTNO", rs.getInt("DEPTNO"));
							resList.add(map);
						}
						
						return resList;
					}
					
				});
				
				for (Map<String, Object> map : resList) {
					for (String key : map.keySet()) {
						System.out.print(map.get(key));
					}
					System.out.println("------一条记录------");
				}
	}
	
	@Test
	public void testInsert(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		JdbcTemplate jdbcTemplate = (JdbcTemplate)ac.getBean("jdbcTemplate");
		
		String sql = "insert into dept(deptno,dname,loc) values (?,?,?)";
		
		try{
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setInt(1, 5);
					ps.setString(2, "test01");
					ps.setString(3, "loc01");
				}
			});
		}catch(Exception e){
			System.out.println(e.getMessage());
			
		}
		
	}
	
	@Test
	public void testBatchInsert() throws SQLException{
		Connection con =null;
		PreparedStatement ps1=null;
		
		try {
			con = DbUtil.getConnection();
			String sql = "";
			ps1 =con.prepareStatement(sql );
			
			insertToTable( ps1, 200);
			ps1.executeBatch();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			DbUtil.close();
			//DbUtil.close(null, ps1, con);
		}
		System.out.println(con);
	}
	
	/**
	 * 
	 * @param ps
	 * @param num	多少条记录插入一次
	 * @throws SQLException
	 */
	public void insertToTable(PreparedStatement ps,int num) throws SQLException {
		
		ps.setInt(1, 1);
		ps.addBatch();
		//5000条执行一次插入
		if(num%5000 ==0){
			ps.executeBatch();
		}
	}
}
