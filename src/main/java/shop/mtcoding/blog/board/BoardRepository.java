package shop.mtcoding.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BoardRepository {
    private final EntityManager em;

    public List<Board> findAll(int page){
        final int COUNT = 5;
        int value = page*5;
        Query query = em.createNativeQuery("select * from board_tb order by id desc limit ?,?", Board.class);
        query.setParameter(1, value);
        query.setParameter(2, COUNT);

        List<Board> boardList = query.getResultList();

        return boardList;
    }

    public List<Board> findAll(int page, String keyword) {
        final int COUNT = 5;
        int value = page*5;
        Query query = em.createNativeQuery("select * from board_tb where title like ? order by id desc limit ?, ?", Board.class);
        query.setParameter(1, "%" + keyword + "%");
        query.setParameter(2, value);
        query.setParameter(3, COUNT);

        List<Board> boardList = query.getResultList();

        return boardList;
    }


    @Transactional
    public void save(BoardRequest.SaveDTO requestDTO) {
        Query query = em.createNativeQuery("insert into board_tb(author, title, content) values(?,?,?)");
        query.setParameter(1, requestDTO.getAuthor());
        query.setParameter(2, requestDTO.getTitle());
        query.setParameter(3, requestDTO.getContent());

        query.executeUpdate();
    }

    public Board findById(int id){
        Query query = em.createNativeQuery("select * from board_tb where id = ?", Board.class);
        query.setParameter(1, id);

        Board board = (Board) query.getSingleResult();
        return board;
    }

    @Transactional
    public void update(BoardRequest.UpdateDTO requestDTO, int id) {
        Query query = em.createNativeQuery("update board_tb set author=?, title=?, content=? where id = ?");
        query.setParameter(1, requestDTO.getAuthor());
        query.setParameter(2, requestDTO.getTitle());
        query.setParameter(3, requestDTO.getContent());
        query.setParameter(4, id);

        query.executeUpdate();
    }

    @Transactional
    public void deleteById(int id) {
        Query query = em.createNativeQuery("delete from board_tb where id = ?");
        query.setParameter(1, id);
        query.executeUpdate();
    }

    public int count(){
        Query query = em.createNativeQuery("select count(*) from board_tb");

        Long count = (Long) query.getSingleResult();

        return count.intValue();
    }

    public int count(String keyword){
        Query query = em.createNativeQuery("select count(*) from board_tb where title like ? ");
        query.setParameter(1, "%" + keyword + "%");

        Long count = (Long) query.getSingleResult();

        return count.intValue();
    }

}