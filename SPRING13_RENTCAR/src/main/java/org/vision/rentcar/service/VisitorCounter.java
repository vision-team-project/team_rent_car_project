package org.vision.rentcar.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.ibatis.session.SqlSession;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.vision.rentcar.dao.VisitorDAO;
import org.vision.rentcar.model.RentVisitor;

public class VisitorCounter implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent source) {
		HttpSession session = source.getSession();
		System.out.println(session + "세션이 생성되었습니다.");

		WebApplicationContext wac = WebApplicationContextUtils
				.getRequiredWebApplicationContext(session.getServletContext());
		// 등록되어있는 빈을 사용할수 있도록 설정해준다
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		// request를 파라미터에 넣지 않고도 사용할수 있도록 설정
		SqlSession SqlSession = (SqlSession) wac.getBean("sqlSession");
		VisitorDAO visitorDAO = SqlSession.getMapper(VisitorDAO.class);
		// (VisitorDAO)wac.getBean("rentVisitor");

		System.out.println(SqlSession + "카운터에서 만든 sqlsession");
		String ua = req.getHeader("User-Agent");
		String browser = "";

		if (ua.indexOf("Chrome") > -1) {
			browser = "Chrome";
		} else if (ua.indexOf("Safari") > -1 && ua.indexOf("Chrome") == -1) {
			browser = "Safari";
		} else if (ua.indexOf("Edge") > -1) {
			browser = "Edge";
		} else if (ua.indexOf("Whale") > -1) {
			browser = "Whale/naver";
		} else if (ua.indexOf("Opera") > -1 || ua.indexOf("OPR") > -1) {
			browser = "Opera";
		} else if (ua.indexOf("Firefox") > -1) {
			browser = "Firefox";
		} else if (ua.indexOf("Trident") > -1) {
			browser = "Ie";
		} else {
			browser = "Unknown";
		}

		RentVisitor vo = new RentVisitor();
		vo.setVisit_ip(req.getRemoteAddr());
		vo.setVisit_agent(browser);// 브라우저 정보
		vo.setVisit_site(req.getHeader("referer"));// 접속 전 사이트 정보

		System.out.println(req.getRemoteAddr() + "######" + browser + "######" + req.getHeader("referer"));

		visitorDAO.insertVisitor(vo);

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		System.out.println("session 삭제되었습니다");
	}
}
