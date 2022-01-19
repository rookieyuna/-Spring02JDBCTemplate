package springboard.command;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;

/*
BbsCommandImpl 인터페이스를 구현했으므로 execute()는
반드시 오버라이딩해야한다. 
또한 해당객체는 부모타입인 BbsCommandImpl로 참조할 수 있다.
 */
public class ListCommand implements BbsCommandImpl {
	
	@Override
	public void execute(Model model) {
	
		System.out.println("ListCommand > execute() 호출");
		
		/*
		컨트롤러에서 인자로 전달한 Model객체에는 request객체가 저장되어 있다.
		asMap()을 통해 Map컬렉션으로 변환한 후 모든 요청을 얻어온다.
		 */
		Map<String, Object> paramMap = model.asMap();
		//현재 Object형으로 저장된 request객체를 원래타입으로 형변환해준다
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		//DAO 객체 생성
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		
		//검색어 처리 
		String addQueryString ="";
		//request내장객체를 통해 폼값을 받아온다.
		String searchColumn = req.getParameter("searchColumn");
		String searchWord = req.getParameter("searchWord");
		
		if(searchWord != null) {
			//검색어가 있는 경우 쿼리스트링 추가
			addQueryString = String.format("searchColumn=%s&searchWord=%s&",searchColumn,searchWord);
			
			//DAO로 전달한 데이터를 Map컬렉션에 저장
			paramMap.put("Column", searchColumn);
			paramMap.put("Word", searchWord);
		}
		
		//전체 레코드 수 카운트하기
		int totalRecordCount = dao.getTotalCount(paramMap);
		
		//실제 출력할 게시물 select한후 반환받음(페이징x)
		ArrayList<SpringBbsDTO> listRows = dao.list(paramMap);
		
		//목록에 출력할 게시물 가상번호 계산하여 부여하기
		int virtualNum = 0;
		int countNum = 0;
		
		for(SpringBbsDTO row : listRows) {
			//전체게시물의 개수에서 하나씩 차감하면서 가상번호 부여한다.(페이징x)
			virtualNum = totalRecordCount --;
			
			//가상번호를 setter로 저장
			row.setVirtualNum(virtualNum);
		}
		
		//위에서 처리한 목록의 모든 처리결과를 Model객체에 저장한다.
		model.addAttribute("listRows", listRows);
		
		//JdbcTemplate을 사용할 때는 자원반납은 하지않는다. 
		//dao.close();
			
	}
}
