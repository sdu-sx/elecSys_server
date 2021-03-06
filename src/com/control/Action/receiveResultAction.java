package com.control.Action;

/**
 * 名称: receiveResultAction
 * 描述: 该类用于处理服务器接收设备
 * 类型: JAVA
 * @author 李昌健
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.Dao.Result;
import com.Dao.ResultDAO;
import com.Dao.ResultId;
import com.Dao.Task;
import com.Dao.TaskDAO;
import com.db.HibernateSessionFactory;
import com.opensymphony.xwork2.ActionSupport;

public class receiveResultAction extends ActionSupport implements
		ServletRequestAware, ServletResponseAware {

	private HttpServletRequest request;
	private HttpServletResponse response;

	private String tid;
	private String did;
	private String cid;
	private String value;
	private JSONArray ItemList;
	private String result;
	private String feadBack;
	private ResultDAO dao = new ResultDAO();
	private JSONObject jsonObject;
	private Session session = HibernateSessionFactory.getSession();
	private Transaction tx = session.beginTransaction();
	private TaskDAO tdao = new TaskDAO();


	  /**
	　　　 * 方法描述
	　　　 * 
		* 变量的set get群
	　　　 *
	　　　 */
	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**从父类继承的方法需要实现**/
	public void setServletRequest(HttpServletRequest arg0) {
		// TODO Auto-generated method stub
		request = arg0;
	}

	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		response = arg0;
	}


	  /**
	　　　 * 方法描述
	　　　 * 
	　　　 * @param String did tid content
	　　　 * @return json
		* 服务器记录设备检查信息并回传相应的信息
	　　　 *
	　　　 */
	public void writeResult() {
		try {

			this.response.setContentType("text/json;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");

			result = request.getParameter("parameter");
			jsonObject = new JSONObject(result);

			tid = jsonObject.getString("tid");
			did = jsonObject.getString("did");
			ItemList = jsonObject.getJSONArray("clauselist");
			
			Task t = tdao.findById(tid);
			if (t == null) {
				feadBack = "\"no such task\"";
			} else {
				String dec = t.getDevices();
				String str[] = new String[10];
				str = dec.split("@");
				boolean flag = false;
				for (int i = 1; i < str.length; i++) {
					if (str[i].substring(0, 1).equals(did)) {
						flag = true;
					}
				}

				if (!flag) {
					feadBack = "\"no such device\"";
				} else {					
					for (int i = 0; i < ItemList.length(); i++) {
						Result rt = new Result();
						ResultId id = new ResultId();
						id.setDid(did);
						id.setTid(tid);
						id.setCid(ItemList.getJSONObject(i).getString("cid"));
						rt.setId(id);
						rt.setValue(ItemList.getJSONObject(i).getString("value"));
						dao.merge(rt);
						System.out.println("log the result in the result table");						
					}
					
					tx.commit();
					session.close();
					feadBack = "success";
				}
			}
			
			/**f发送json**/
			response.getWriter().write(feadBack);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
