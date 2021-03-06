package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.RequestDetailService;
import vo.ActionForward;
import vo.RequestBean;

public class RequestDetailAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("RequestDetailAction");
		
		ActionForward forward = null;
		
		int num = Integer.parseInt(request.getParameter("board_num"));
		
		RequestDetailService boardDetailService = new RequestDetailService();
		RequestBean article = boardDetailService.getArticle(num);
		
		request.setAttribute("article", article);
		
		forward = new ActionForward();
		forward.setPath("/sub4/request_board_view.jsp");
		
		return forward;
	}

}
