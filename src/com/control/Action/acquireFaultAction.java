package com.control.Action;

/**
 * 名称: acquireFaultAction
 * 描述: 该类用于处理客户端获取设备缺陷的请求
 * 类型: JAVA
 * @author 李昌健
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.Dao.Fault;
import com.Dao.FaultDAO;
import com.Dao.Task;
import com.opensymphony.xwork2.ActionSupport;

public class acquireFaultAction extends ActionSupport implements
		ServletRequestAware, ServletResponseAware {

	private HttpServletRequest request;
	private HttpServletResponse response;

	private String did; // 获取的设备号
	private Date time; // 基准时间
	private FaultDAO fdao = new FaultDAO();
	private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private Map<String, String> result = new HashMap<String, String>();
	private List<Fault> flist;
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


	  /**
	　　　 * 方法描述
	　　　 * 
		* 变量的set get群
	　　　 *
	　　　 */
	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
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
	　　　 * @param String did
	　　　 * @return json
		* 服务器返回给andriod设备的缺陷信息
	　　　 *
	　　　 */
	public void getFault() {
		try {
			this.response.setContentType("text/json;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");

			System.out.println(did);
			
			/**设备不存在**/
			if (did == null) {
				result.put("message", "\"no such device\"");
			} else {
				flist = fdao.findAll();
				int j = flist.size();
				for(int i=0;i<j;){
					Fault tt = flist.get(i);
					if(tt.getDid().substring(0, 1).equals(did) == false){
						flist.remove(tt);
						j--;
					}else{
						i++;
					}
				}
				
				/**设备没有缺陷**/
				if (flist.size() <= 0) {
					result.put("message", "\"no such device\"");
				} else {
					/**时间不为空**/
					if (time != null) {
						Fault f;
						for (int i = 0; i < flist.size(); i++) {
							Map<String, String> json = new HashMap<String, String>();
							f = flist.get(i);
							if ((f.getTime().equals(time)||f.getTime().after(time))
									&& f.getSolved().equals("否")) {
								json.put("fid", Integer.toString(f.getFid()));
								json.put("content", "\""+f.getContent()+"\"");
								json.put("time", df.format(f.getTime()));
								list.add(json);
							}
						}
						result.put("message", "success");
						result.put("faultlist", list.toString());
					} else {
						Fault f;
						for (int i = 0; i < flist.size(); i++) {
							Map<String, String> json = new HashMap<String, String>();
							f = flist.get(i);
							if (f.getSolved().equals("否")) {
								json.put("fid", Integer.toString(f.getFid()));
								json.put("content", "\""+f.getContent()+"\"");
								json.put("time", df.format(f.getTime()));
								list.add(json);
							}
						}
						result.put("message", "success");
						result.put("faultlist", list.toString());
					}
				}
			}
			/**发送json数据**/
			byte[] jsonBytes = result.toString().getBytes("utf-8");
			response.setContentLength(jsonBytes.length);
			response.getOutputStream().write(jsonBytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
