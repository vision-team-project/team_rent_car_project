package org.vision.rentcar;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vision.rentcar.dao.MemberDAO;
import org.vision.rentcar.model.RentMember;
import org.vision.rentcar.serviceMember.Constant;
import org.vision.rentcar.serviceMember.FindIdService;
import org.vision.rentcar.serviceMember.FindPwService;
import org.vision.rentcar.serviceMember.IdCheckService;
import org.vision.rentcar.serviceMember.JoinService;
import org.vision.rentcar.serviceMember.ModifyService;
import org.vision.rentcar.serviceMember.ServiceCheck;
import org.vision.rentcar.serviceMember.ServiceMember;
import org.vision.rentcar.serviceMember.batchService;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping({"/member/*","/admin/*","/car/*","/reserve/*","/board/**"})
public class MemberController {
	
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	private SqlSession sqlSession;
	private ServiceMember service;
	private ServiceCheck service2;
	
	@Autowired
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
		Constant.sqlSession = this.sqlSession;
	}
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("MemberController ??????. The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping("/memList")
	public String memList(Model model) {	//?????? ??????
		MemberDAO dao = sqlSession.getMapper(MemberDAO.class);
		model.addAttribute("memList", dao.memList());
		logger.info("memList ??????.");
		return "/member/memList";	//memList.jsp ??????
	}
	
	@RequestMapping("/myList")
	public String myList(Model model,HttpServletRequest request,HttpSession session) {	//??????????????????
		MemberDAO dao = sqlSession.getMapper(MemberDAO.class);
		RentMember mypage = dao.myList(request.getParameter("id"));
		model.addAttribute("mypage", mypage);
		session.setAttribute("mypage", mypage);
		logger.info("myList ??????.");
		return "/member/myList";	//myList.jsp ??????
	}
	
	//???????????? memList?????? ??????
	@RequestMapping(value="/memDelete",method = RequestMethod.GET)
	public String memDelete(HttpServletRequest request) {
		MemberDAO dao = sqlSession.getMapper(MemberDAO.class);
		dao.delete(request.getParameter("id"));
		logger.info("memModify ??????.");
		return "redirect:memList";
	}
	
	@RequestMapping(value="/memModify",method = RequestMethod.GET)
	public String memModify(Model model,HttpServletRequest request) {
		model.addAttribute("request", request);
		service = new ModifyService();
		service.execute(model);
		logger.info("memModify ??????.");
		return "redirect:memList";
	}
	
	//????????? myList?????? ??????
	@RequestMapping(value="/myDelete",method = RequestMethod.GET)
	public String myDelete(HttpServletRequest request) {
		MemberDAO dao = sqlSession.getMapper(MemberDAO.class);
		dao.delete(request.getParameter("id"));
		logger.info("myDelete ??????.");
		return "redirect:/member/loginForm";
	}
	
	@RequestMapping("/myModify")
	public String myModify(Model model,HttpServletRequest request,RentMember dto) {
		model.addAttribute("request", request);
		service = new ModifyService();
		service.execute(model);
		logger.info("myModify ??????.");
		return "redirect:/member/myList?id="+dto.getId();
	}
	
	@RequestMapping("/joinForm")
	public String joinForm() {
		logger.info("joinForm ??????.");
		return "/member/joinForm";	//joinForm.jsp ??????
	}
	
	@RequestMapping("/join")
	public String join(Model model,HttpServletRequest request,HttpServletResponse response) {
		model.addAttribute("request", request);
		model.addAttribute("response", response);
		service = new JoinService();
		service.execute(model);
		logger.info("join ??????.");
		return "redirect:loginForm";
	}
	
	@RequestMapping("/loginForm")
	public String loginForm() {
		logger.info("loginForm ??????.");
		return "/member/loginForm";	//loginForm.jsp ??????
	}
	
	@ResponseBody
	@RequestMapping(value="/idCheck",method = RequestMethod.GET)
    public String idCheck(@RequestParam("id") String id){
		service2 = new IdCheckService();
		RentMember m = service2.check(id);
		String message = null;
		if(m==null) {
			message = "success";
		} else {
			message = "fail";
		}
		return message;
    }
	
	@RequestMapping("/login")
	public String login(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException {
//		model.addAttribute("request", request);
//		model.addAttribute("response", response);
//		model.addAttribute("session", session);
//		service = new loginService();
//		service.action(model);
		String returnURL = "";
		
		MemberDAO dao = sqlSession.getMapper(MemberDAO.class);
		//?????? ????????? ???????????? ??????????????? ?????? ????????????
		if(session.getAttribute("login") != null) {
			session.removeAttribute("login");
		}
		//????????? ?????? ??????
		RentMember dto = new RentMember();
		dto.setId(request.getParameter("id"));
		dto.setPass(request.getParameter("pass"));
		session.setMaxInactiveInterval(60*60);//60???
		//????????? ?????? ??????
		RentMember login = dao.getMember(dto);
		if(login != null) {	//????????? ??????!
			session.setAttribute("login", login);	//login??? login????????? ???????????? ????????? ??????
			session.setAttribute("id", dto.getId());
			System.out.println(dto.getId()+"????????? ??????.");
			returnURL = "home";
		} else {	//????????? ??????!
			System.out.println("????????? ??????.");
//			response.setContentType("text/html; charset=UTF-8");
//			PrintWriter out = response.getWriter();
//			out.println("<script>alert('???????????? ????????? ??????????????? ????????????.'); location.href='loginForm';</script>");
//			out.flush();
			returnURL = "/member/loginFail";
		}
		return returnURL;
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();	//?????? ??????
		System.out.println("???????????? ??????.");
		return "redirect:/";
	}

	@RequestMapping("/batchForm")
	public String batchForm() {
		logger.info("batchForm ??????");
		return "/member/batchForm";	//batchForm.jsp ??????
	}
	
	@RequestMapping("/batch")
	public String batch(Model model,HttpServletRequest request) {
		model.addAttribute("request", request);
		service = new batchService();
		service.execute(model);
		logger.info("batch ??????");
		return "redirect:/member/memList";
	}
	
	@RequestMapping("/findIdForm")
	public String findIdForm() {
		logger.info("findIdForm ??????");
		return "/member/findIdForm";
	}
	
	@RequestMapping("/findId")
	public String findId(Model model,HttpServletResponse response, @RequestParam("email") String email) throws Exception{
		service2 = new FindIdService();
		model.addAttribute("id", service2.execute(response, email));
		logger.info("findId ??????");
		return "/member/findId";
	}
	
	@RequestMapping("/findPwForm")
	public String findPwForm() {
		logger.info("findPwForm ??????");
		return "/member/findPwForm";
	}
	
	@RequestMapping("/findPw")
	public void findPw(@ModelAttribute RentMember dto,HttpServletResponse response) throws Exception{
		service2 = new FindPwService();
		service2.execute2(response, dto);
		logger.info("findPw ??????");
	}
	

}
