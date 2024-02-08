package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final HttpSession session;
    private final BoardRepository boardRepository;

    @GetMapping({ "/", "/board" })
    public String index(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String keyword) {

        List<Board> boardList = new ArrayList<>();
        if(keyword.isBlank()){
            boardList = boardRepository.findAll(page);
        }else{
            boardList = boardRepository.findAll(page, keyword);
        }
        request.setAttribute("boardList", boardList);

        int currentPage = page;
        int nextPage = currentPage + 1;
        int prevPage = currentPage - 1;
        request.setAttribute("nextPage", nextPage);
        request.setAttribute("prevPage", prevPage);
        boolean first = (currentPage == 0 ? true : false);

        int totalCount = boardRepository.count();
        int paging = 5;
        int totalPage = totalCount / paging;
        if (totalCount % paging != 0) {
            totalPage = 1 + (totalCount / paging);
        }
        ArrayList pageIndex = new ArrayList();
        for(int i=0; i<totalPage; i++){
            pageIndex.add(i);
        }
        request.setAttribute("pageIndex", pageIndex);

        boolean last = currentPage == (totalPage - 1) ? true : false;

        request.setAttribute("first", first);
        request.setAttribute("last", last);

        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        return "board/saveForm";
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request) {
        Board board = boardRepository.findById(id);
        request.setAttribute("board", board);

        return "board/updateForm";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request){
        System.out.println(requestDTO);

        if (requestDTO.getTitle().length() > 20) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "title의 길이가 20자를 초과해서는 안 됩니다");
            return "error/40x"; // BadRequest
        }
        if (requestDTO.getContent().length() > 20) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "content의 길이가 20자를 초과해서는 안 됩니다");
            return "error/40x"; // BadRequest
        }

        boardRepository.save(requestDTO);

        return "redirect:/";
    }

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO){

        // update board_tb set title = ?, content = ? where id = ?;
        boardRepository.update(requestDTO, id);

        return "redirect:/";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id){

        boardRepository.deleteById(id);

        return "redirect:/";
    }

}