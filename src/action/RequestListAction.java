package action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import svc.RequestListService;
import vo.ActionForward;
import vo.PageInfo;
import vo.RequestBean;

public class RequestListAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// RequestListService 클래스를 통해 게시물 목록 조회 후
		// 조회 결과를 request 객체에 저장하고, qna_board_list.jsp 페이지로 포워딩
		// => 이 때, request 객체를 유지하고, 서블릿 주소의 변경 없이
		//    포워딩 해야하므로 포워딩 방식을 Dispatcher 방식으로 지정
		System.out.println("BoardListAction");
		
		ActionForward forward = null;

		int page = 1;
		int limit = 10;

		if(request.getParameter("page") != null) {
			page = Integer.parseInt(request.getParameter("page"));
		}

		RequestListService requestListService = new RequestListService();
		int listCount = requestListService.getListCount();
		
		ArrayList<RequestBean> articleList = new ArrayList<RequestBean>();
		
		articleList = requestListService.getArticleList(page, limit);	

		int maxPage = (int)((double) listCount / limit + 0.95);

		int startPage = ((int)((double)page / 10 + 0.9) - 1) * 10 + 1;

		int endPage = startPage + 10 - 1;

		if(endPage > maxPage) {
			endPage = maxPage;
		}
		
		PageInfo pageInfo = new PageInfo(page, maxPage, startPage, endPage, listCount);

		request.setAttribute("articleList", articleList);
		request.setAttribute("pageInfo", pageInfo);

		forward = new ActionForward();
		forward.setPath("/sub4/request_board.jsp");
		
		return forward;
	}

}
