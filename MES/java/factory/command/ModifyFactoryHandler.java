package factory.command;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import auth.service.User;
import member.command.CommandHandler;
import factory.service.ModifyFactoryService;
import factory.model.Factory;
import factory.service.ModifyFactoryRequest;
import factory.service.FactoryNotFountException;
import factory.service.PermissionDeniedException;

public class ModifyFactoryHandler implements CommandHandler{
	
	private static final String FORM_VIEW = "/WEB-INF/view/FactoryModify.jsp";
	
	private ModifyFactoryService modifyService = new ModifyFactoryService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(req.getMethod().equalsIgnoreCase("GET")) {
			return processForm(req,res);
		} else if(req.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(req, res);
		} else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}

	private String processForm(HttpServletRequest req, HttpServletResponse res) throws IOException, ParseException {
		  try { 
		  int noVal = Integer.parseInt(req.getParameter("no")); 
		  Factory loadData = modifyService.loadData(noVal); 
		  User user = (User)
		  req.getSession().getAttribute("authUser");
		  
		  if(!canModify(user)) { 
			  res.sendError(HttpServletResponse.SC_FORBIDDEN);
			  return FORM_VIEW; 
		  }
		  req.setAttribute("factorydata", loadData);
		  return FORM_VIEW; 
		  } catch(FactoryNotFountException e) {
		  res.sendError(HttpServletResponse.SC_NOT_FOUND);
		  return null;
		  }
		 
	}
	
	private boolean canModify(User user) {
		return true;
	}
	
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date today = new Date();
		String strToday = sdf.format(today);
		User user = (User) req.getSession().getAttribute("authUser");
		int cdVal = Integer.parseInt(req.getParameter("plant_cd"));
		String nmVal = req.getParameter("plant_nm");
		String remarkVal = req.getParameter("remark");
		Date valid_fr_Val = sdf.parse(req.getParameter("valid_fr_dt"));
		Date valid_to_Val = sdf.parse(req.getParameter("valid_to_dt"));
		Date up_dateVal = sdf.parse(strToday);
		String UPUSRVal = user.getId(); //수정자ID
		
		ModifyFactoryRequest modReq = new ModifyFactoryRequest(cdVal, nmVal, valid_fr_Val, valid_to_Val, remarkVal, UPUSRVal,  up_dateVal);
		req.setAttribute("modReq", modReq);
		
		
		Map<String, Boolean> errors = new HashMap<String, Boolean>();
		req.setAttribute("errors", errors);
		if(!errors.isEmpty()) {
			return FORM_VIEW;
		}
		try {
			modifyService.modify(modReq);
			return "factorylist.do";
		} catch (FactoryNotFountException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (PermissionDeniedException e) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
	}

}
